package com.foxconn.iad.module.esd.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.foxconn.iad.module.esd.common.PageResponse;
import com.foxconn.iad.module.esd.controller.admin.person.vo.PersonProfileCreateReqVO;
import com.foxconn.iad.module.esd.controller.admin.person.vo.PersonProfilePageReqVO;
import com.foxconn.iad.module.esd.controller.admin.person.vo.PersonProfileRespVO;
import com.foxconn.iad.module.esd.controller.admin.person.vo.PersonProfileUpdateReqVO;
import com.foxconn.iad.module.esd.dal.dataobject.AssetDO;
import com.foxconn.iad.module.esd.dal.dataobject.PersonProfileDO;
import com.foxconn.iad.module.esd.dal.mapper.AssetMapper;
import com.foxconn.iad.module.esd.dal.mapper.PersonProfileMapper;
import com.foxconn.iad.module.esd.exception.BusinessException;
import com.foxconn.iad.module.esd.platform.PlatformDeptGateway;
import com.foxconn.iad.module.esd.platform.PlatformDeptResp;
import com.foxconn.iad.module.esd.platform.PlatformUserGateway;
import com.foxconn.iad.module.esd.platform.PlatformUserResp;
import com.foxconn.iad.module.esd.security.LoginUserContext;
import com.foxconn.iad.module.esd.security.SiteAccessService;
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
public class PersonProfileServiceImpl implements PersonProfileService {

    /** ESD 人員檔 Mapper。 */
    private final PersonProfileMapper personProfileMapper;
    /** 用於統計員工當前持有的衣鞋數量。 */
    private final AssetMapper assetMapper;
    /** 平台用戶狀態和廠區歸屬校驗。 */
    private final PlatformUserGateway platformUserGateway;
    /** 平台部門狀態校驗及名稱快照查詢。 */
    private final PlatformDeptGateway platformDeptGateway;
    /** 當前用戶廠區權限和操作人信息。 */
    private final SiteAccessService siteAccessService;

    @Override
    @Transactional
    public Long create(PersonProfileCreateReqVO request) {
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
            throw new BusinessException(400, "該員工已綁定當前廠區");
        }
        return profile.getId();
    }

    @Override
    @Transactional
    public void update(Long id, PersonProfileUpdateReqVO request) {
        LoginUserContext operator = siteAccessService.requireSite(request.getSiteCode());
        PersonProfileDO profile = requireProfile(id, request.getSiteCode());
        // 停用人員前必須先清理持有資產，否則後續發放無法找到有效業務人員。
        if (Integer.valueOf(0).equals(request.getEsdStatus())) {
            long issuedCount = countHeldAssets(profile.getPlatformUserId(), request.getSiteCode(), null);
            if (issuedCount > 0) {
                throw new BusinessException(400, "員工仍持有資產，不能停用 ESD 人員檔");
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

    @Override
    public PageResponse<PersonProfileRespVO> page(PersonProfilePageReqVO request) {
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
        List<PersonProfileRespVO> list = new ArrayList<>();
        for (PersonProfileDO profile : page.getRecords()) {
            list.add(toResponse(profile));
        }
        return new PageResponse<>(list, page.getTotal());
    }

    @Override
    public PersonProfileRespVO get(Long id, String siteCode) {
        siteAccessService.requireSite(siteCode);
        return toResponse(requireProfile(id, siteCode));
    }

    @Override
    public PersonProfileRespVO requireEnabledByUser(String siteCode, Long platformUserId) {
        siteAccessService.requireSite(siteCode);
        PersonProfileDO profile = personProfileMapper.selectOne(new LambdaQueryWrapper<PersonProfileDO>()
                .eq(PersonProfileDO::getSiteCode, siteCode)
                .eq(PersonProfileDO::getPlatformUserId, platformUserId));
        if (profile == null) {
            throw new BusinessException(404, "員工未綁定當前廠區");
        }
        if (!Integer.valueOf(1).equals(profile.getEsdStatus())) {
            throw new BusinessException(400, "員工 ESD 人員檔已停用");
        }
        return toResponse(profile);
    }

    /** 讀取指定廠區內的人員檔，不允許僅憑全局 ID 查詢。 */
    private PersonProfileDO requireProfile(Long id, String siteCode) {
        PersonProfileDO profile = personProfileMapper.selectOne(
                new LambdaQueryWrapper<PersonProfileDO>()
                        .eq(PersonProfileDO::getId, id)
                        .eq(PersonProfileDO::getSiteCode, siteCode));
        if (profile == null) {
            throw new BusinessException(404, "人員檔不存在");
        }
        return profile;
    }

    /** 組裝 API 響應，並實時統計當前持有的衣鞋數量。 */
    private PersonProfileRespVO toResponse(PersonProfileDO profile) {
        PersonProfileRespVO response = new PersonProfileRespVO();
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

    /** 統計當前處於「已發放」狀態的資產，用於人員列表和停用校驗。 */
    private long countHeldAssets(Long userId, String siteCode, Integer assetType) {
        return assetMapper.selectCount(new LambdaQueryWrapper<AssetDO>()
                .eq(AssetDO::getSiteCode, siteCode)
                .eq(AssetDO::getCurrentHolderUserId, userId)
                // 20 是已發放狀態；這裡使用與資料庫約束一致的穩定編碼。
                .eq(AssetDO::getLifecycleStatus, 20)
                .eq(assetType != null, AssetDO::getAssetType, assetType));
    }

    /** 將空白字符串統一轉換為 null，保持資料庫快照字段語義一致。 */
    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
