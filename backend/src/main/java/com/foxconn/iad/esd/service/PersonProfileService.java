package com.foxconn.iad.esd.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.foxconn.iad.esd.common.PageResponse;
import com.foxconn.iad.esd.controller.dto.person.PersonProfileCreateReq;
import com.foxconn.iad.esd.controller.dto.person.PersonProfilePageReq;
import com.foxconn.iad.esd.controller.dto.person.PersonProfileResp;
import com.foxconn.iad.esd.controller.dto.person.PersonProfileUpdateReq;
import com.foxconn.iad.esd.dal.dataobject.AssetDO;
import com.foxconn.iad.esd.dal.dataobject.PersonProfileDO;
import com.foxconn.iad.esd.dal.mapper.AssetMapper;
import com.foxconn.iad.esd.dal.mapper.PersonProfileMapper;
import com.foxconn.iad.esd.exception.BusinessException;
import com.foxconn.iad.esd.platform.PlatformUserGateway;
import com.foxconn.iad.esd.platform.PlatformUserResp;
import com.foxconn.iad.esd.platform.PlatformDeptGateway;
import com.foxconn.iad.esd.platform.PlatformDeptResp;
import com.foxconn.iad.esd.security.LoginUserContext;
import com.foxconn.iad.esd.security.SiteAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonProfileService {

    /** ESD 人員檔 Mapper。 */
    private final PersonProfileMapper personProfileMapper;
    /** 用於統計員工當前持有的衣鞋數量。 */
    private final AssetMapper assetMapper;
    /** 平台用戶狀態和廠區歸屬校驗。 */
    private final PlatformUserGateway platformUserGateway;
    private final PlatformDeptGateway platformDeptGateway;
    private final SiteAccessService siteAccessService;

    @Transactional
    public Long create(PersonProfileCreateReq request) {
        LoginUserContext operator = siteAccessService.requireSite(request.getSiteCode());
        // 用戶和主管必須都屬於當前廠區，避免跨廠區建立業務關係。
        PlatformUserResp user = platformUserGateway.requireEnabledUser(
                request.getPlatformUserId(), request.getSiteCode());
        PlatformDeptResp dept = platformDeptGateway.requireEnabledDept(user.getDeptId());
        PlatformUserResp supervisor = request.getSupervisorUserId() == null ? null
                : platformUserGateway.requireEnabledUser(request.getSupervisorUserId(), request.getSiteCode());

        PersonProfileDO profile = new PersonProfileDO();
        profile.setSiteCode(request.getSiteCode());
        profile.setPlatformUserId(user.getId());
        profile.setEmployeeNo(user.getUsername());
        profile.setEmployeeName(user.getNickname());
        profile.setDeptId(user.getDeptId());
        profile.setDeptName(dept == null ? null : dept.getName());
        profile.setSupervisorUserId(supervisor == null ? null : supervisor.getId());
        profile.setSupervisorName(supervisor == null ? null : supervisor.getNickname());
        profile.setFloorCode(normalize(request.getFloorCode()));
        profile.setShiftCode(normalize(request.getShiftCode()));
        profile.setEsdStatus(1);
        profile.setCreator(String.valueOf(operator.getUserId()));
        profile.setUpdater(String.valueOf(operator.getUserId()));
        profile.setCreateTime(LocalDateTime.now());
        profile.setUpdateTime(LocalDateTime.now());
        try {
            personProfileMapper.insert(profile);
        } catch (DuplicateKeyException exception) {
            // 資料庫唯一索引是最終防線，處理重複提交或並發綁定。
            throw new BusinessException(400, "该员工已绑定当前厂区");
        }
        return profile.getId();
    }

    @Transactional
    public void update(Long id, PersonProfileUpdateReq request) {
        LoginUserContext operator = siteAccessService.requireSite(request.getSiteCode());
        PersonProfileDO profile = requireProfile(id, request.getSiteCode());
        // 停用人員前必須先清理持有資產，否則後續發放無法找到有效業務人員。
        if (Integer.valueOf(0).equals(request.getEsdStatus())) {
            long issuedCount = countHeldAssets(profile.getPlatformUserId(), request.getSiteCode(), null);
            if (issuedCount > 0) {
                throw new BusinessException(400, "员工仍持有资产，不能停用 ESD 人员档");
            }
        }
        PlatformUserResp supervisor = request.getSupervisorUserId() == null ? null
                : platformUserGateway.requireEnabledUser(request.getSupervisorUserId(), request.getSiteCode());
        profile.setSupervisorUserId(supervisor == null ? null : supervisor.getId());
        profile.setSupervisorName(supervisor == null ? null : supervisor.getNickname());
        profile.setFloorCode(normalize(request.getFloorCode()));
        profile.setShiftCode(normalize(request.getShiftCode()));
        if (request.getEsdStatus() != null) {
            profile.setEsdStatus(request.getEsdStatus());
        }
        profile.setUpdater(String.valueOf(operator.getUserId()));
        profile.setUpdateTime(LocalDateTime.now());
        personProfileMapper.updateById(profile);
    }

    public PageResponse<PersonProfileResp> page(PersonProfilePageReq request) {
        siteAccessService.requireSite(request.getSiteCode());
        // 先拼接廠區 AND，再拼接關鍵字括號，保證 OR 查詢不會越權。
        LambdaQueryWrapper<PersonProfileDO> query = new LambdaQueryWrapper<PersonProfileDO>()
                .eq(PersonProfileDO::getSiteCode, request.getSiteCode())
                .eq(request.getDeptId() != null, PersonProfileDO::getDeptId, request.getDeptId())
                .eq(request.getEsdStatus() != null, PersonProfileDO::getEsdStatus, request.getEsdStatus())
                .and(StringUtils.hasText(request.getKeyword()), wrapper -> wrapper
                        .like(PersonProfileDO::getEmployeeNo, request.getKeyword())
                        .or().like(PersonProfileDO::getEmployeeName, request.getKeyword()))
                .orderByDesc(PersonProfileDO::getCreateTime);
        IPage<PersonProfileDO> page = personProfileMapper.selectPage(
                new Page<>(request.getPageNo(), request.getPageSize()), query);
        List<PersonProfileResp> list = new ArrayList<>();
        for (PersonProfileDO profile : page.getRecords()) {
            list.add(toResponse(profile));
        }
        return new PageResponse<>(list, page.getTotal());
    }

    public PersonProfileResp get(Long id, String siteCode) {
        siteAccessService.requireSite(siteCode);
        return toResponse(requireProfile(id, siteCode));
    }

    private PersonProfileDO requireProfile(Long id, String siteCode) {
        PersonProfileDO profile = personProfileMapper.selectOne(
                new LambdaQueryWrapper<PersonProfileDO>()
                        .eq(PersonProfileDO::getId, id)
                        .eq(PersonProfileDO::getSiteCode, siteCode));
        if (profile == null) {
            throw new BusinessException(404, "人员档不存在");
        }
        return profile;
    }

    private PersonProfileResp toResponse(PersonProfileDO profile) {
        PersonProfileResp response = new PersonProfileResp();
        response.setId(profile.getId());
        response.setSiteCode(profile.getSiteCode());
        response.setPlatformUserId(profile.getPlatformUserId());
        response.setEmployeeNo(profile.getEmployeeNo());
        response.setEmployeeName(profile.getEmployeeName());
        response.setDeptId(profile.getDeptId());
        response.setDeptName(profile.getDeptName());
        response.setSupervisorUserId(profile.getSupervisorUserId());
        response.setSupervisorName(profile.getSupervisorName());
        response.setFloorCode(profile.getFloorCode());
        response.setShiftCode(profile.getShiftCode());
        response.setEsdStatus(profile.getEsdStatus());
        response.setGarmentCount(countHeldAssets(profile.getPlatformUserId(), profile.getSiteCode(), 1));
        response.setShoesCount(countHeldAssets(profile.getPlatformUserId(), profile.getSiteCode(), 2));
        response.setCreateTime(profile.getCreateTime());
        response.setUpdateTime(profile.getUpdateTime());
        return response;
    }

    private long countHeldAssets(Long userId, String siteCode, Integer assetType) {
        return assetMapper.selectCount(new LambdaQueryWrapper<AssetDO>()
                .eq(AssetDO::getSiteCode, siteCode)
                .eq(AssetDO::getCurrentHolderUserId, userId)
                // 20 是已發放狀態；這裡使用與資料庫約束一致的穩定編碼。
                .eq(AssetDO::getLifecycleStatus, 20)
                .eq(assetType != null, AssetDO::getAssetType, assetType));
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
    /** 平台部門狀態校驗及名稱快照查詢。 */
    /** 當前用戶廠區權限和操作人信息。 */
    /**
     * 綁定平台用戶為 ESD 業務人員。
     *
     * <p>平台是員工主數據唯一來源，ESD 只保存綁定時的工號、姓名和部門快照，
     * 不保存密碼，也不在本地複製平台用戶表。</p>
     */
    /** 更新人員業務擴展屬性；平台帳號、工號和姓名不能由本地頁面修改。 */
    /** 按廠區、部門、啟停狀態和關鍵字分頁查詢。 */
    /** 查詢指定廠區的人員詳情。 */
    /** 讀取指定廠區內的人員檔，不允許僅憑全局 ID 查詢。 */
    /** 組裝 API 響應，並實時統計當前持有的衣鞋數量。 */
    /** 統計當前處於“已發放”狀態的資產，用於人員列表和停用校驗。 */
    /** 將空白字符串統一轉換為 null，保持資料庫快照字段語義一致。 */
