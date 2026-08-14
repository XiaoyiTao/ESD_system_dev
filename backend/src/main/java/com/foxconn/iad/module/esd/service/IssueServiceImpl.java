package com.foxconn.iad.module.esd.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.foxconn.iad.module.esd.common.PageResult;
import com.foxconn.iad.module.esd.controller.admin.issue.vo.IssueCreateReqVO;
import com.foxconn.iad.module.esd.controller.admin.issue.vo.IssuePageReqVO;
import com.foxconn.iad.module.esd.controller.admin.issue.vo.IssueRespVO;
import com.foxconn.iad.module.esd.controller.admin.person.vo.PersonProfileRespVO;
import com.foxconn.iad.module.esd.convert.IssueConvert;
import com.foxconn.iad.module.esd.dal.dataobject.AssetDO;
import com.foxconn.iad.module.esd.dal.dataobject.AssetEventDO;
import com.foxconn.iad.module.esd.dal.dataobject.IssueRecordDO;
import com.foxconn.iad.module.esd.dal.mysql.AssetEventMapper;
import com.foxconn.iad.module.esd.dal.mysql.AssetMapper;
import com.foxconn.iad.module.esd.dal.mysql.IssueRecordMapper;
import com.foxconn.iad.module.esd.enums.AssetLifecycleStatus;
import com.foxconn.iad.module.esd.enums.BusinessRecordStatus;
import com.foxconn.iad.module.esd.exception.BusinessException;
import com.foxconn.iad.module.esd.security.LoginUserContext;
import com.foxconn.iad.module.esd.security.SiteAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService {

    /** 資產主表 Mapper。 */
    private final AssetMapper assetMapper;
    /** 發放記錄 Mapper。 */
    private final IssueRecordMapper issueRecordMapper;
    /** 資產事件帳 Mapper。 */
    private final AssetEventMapper assetEventMapper;
    /** 人員檔查詢，用於校驗員工並取得快照。 */
    private final PersonProfileService personProfileService;
    /** 廠區權限校驗。 */
    private final SiteAccessService siteAccessService;

    @Override
    @Transactional
    public Long create(IssueCreateReqVO request) {
        LoginUserContext operator = siteAccessService.requireSite(request.getSiteCode());
        // 校驗領用員工並取得工號、姓名、責任主管、課別、樓層快照。
        PersonProfileRespVO person = personProfileService.requireEnabledByUser(
                request.getSiteCode(), request.getEmployeeUserId());
        AssetDO asset = requireAssetByCode(request.getAssetCode(), request.getSiteCode());
        // 條件更新搶佔：僅當資產仍為庫存且版本一致時才發放成功，防止雙重發放。
        LocalDateTime now = LocalDateTime.now();
        AssetDO patch = new AssetDO();
        patch.setLifecycleStatus(AssetLifecycleStatus.ISSUED.getCode());
        patch.setCurrentHolderUserId(person.getPlatformUserId());
        patch.setCurrentHolderNo(person.getEmployeeNo());
        patch.setCurrentHolderName(person.getEmployeeName());
        patch.setVersion(asset.getVersion() + 1);
        patch.setUpdater(String.valueOf(operator.getUserId()));
        patch.setUpdateTime(now);
        int updated = assetMapper.update(patch, new LambdaQueryWrapper<AssetDO>()
                .eq(AssetDO::getId, asset.getId())
                .eq(AssetDO::getSiteCode, request.getSiteCode())
                .eq(AssetDO::getLifecycleStatus, AssetLifecycleStatus.AVAILABLE.getCode())
                .eq(AssetDO::getVersion, asset.getVersion()));
        if (updated != 1) {
            throw new BusinessException(409, "資產已被其他操作更新，發放失敗");
        }
        // 寫入發放記錄並快照員工信息（責任主管/課別/樓層）。
        IssueRecordDO record = new IssueRecordDO();
        record.setRequestId(UUID.randomUUID().toString());
        record.setSiteCode(request.getSiteCode());
        record.setAssetId(asset.getId());
        record.setAssetCode(asset.getAssetCode());
        record.setAssetType(asset.getAssetType());
        record.setEmployeeUserId(person.getPlatformUserId());
        record.setEmployeeNo(person.getEmployeeNo());
        record.setEmployeeName(person.getEmployeeName());
        record.setSupervisorName(person.getSupervisorName());
        record.setDeptName(person.getDeptName());
        record.setFloorCode(person.getFloorCode());
        record.setIssueOperatorUserId(operator.getUserId());
        record.setIssueDate(request.getIssueDate());
        record.setRecordStatus(BusinessRecordStatus.ACTIVE.getCode());
        record.setCreator(String.valueOf(operator.getUserId()));
        record.setCreateTime(now);
        record.setUpdater(String.valueOf(operator.getUserId()));
        record.setUpdateTime(now);
        issueRecordMapper.insert(record);
        writeEvent(request.getSiteCode(), asset, AssetLifecycleStatus.AVAILABLE,
                AssetLifecycleStatus.ISSUED, record.getId(), operator.getUserId(), "ISSUE");
        return record.getId();
    }

    @Override
    public PageResult<IssueRespVO> page(IssuePageReqVO request) {
        siteAccessService.requireSite(request.getSiteCode());
        LambdaQueryWrapper<IssueRecordDO> query = new LambdaQueryWrapper<IssueRecordDO>()
                .eq(IssueRecordDO::getSiteCode, request.getSiteCode())
                .and(StringUtils.hasText(request.getKeyword()), wrapper -> wrapper
                        .like(IssueRecordDO::getEmployeeNo, request.getKeyword())
                        .or().like(IssueRecordDO::getEmployeeName, request.getKeyword())
                        .or().like(IssueRecordDO::getAssetCode, request.getKeyword()))
                .orderByDesc(IssueRecordDO::getCreateTime);
        IPage<IssueRecordDO> page = issueRecordMapper.selectPage(
                new Page<>(request.getPageNo(), request.getPageSize()), query);
        List<IssueRespVO> list = new ArrayList<>();
        for (IssueRecordDO record : page.getRecords()) {
            list.add(IssueConvert.INSTANCE.convert(record));
        }
        return new PageResult<>(list, page.getTotal());
    }

    /** 按編碼查找指定廠區內的資產。 */
    private AssetDO requireAssetByCode(String assetCode, String siteCode) {
        AssetDO asset = assetMapper.selectOne(new LambdaQueryWrapper<AssetDO>()
                .eq(AssetDO::getAssetCode, assetCode)
                .eq(AssetDO::getSiteCode, siteCode));
        if (asset == null) {
            throw new BusinessException(404, "資產不存在");
        }
        return asset;
    }

    /** 追加資產事件帳，記錄狀態流轉與操作人。 */
    private void writeEvent(String siteCode, AssetDO asset, AssetLifecycleStatus before,
                            AssetLifecycleStatus after, Long businessRecordId,
                            Long operatorUserId, String eventType) {
        AssetEventDO event = new AssetEventDO();
        event.setSiteCode(siteCode);
        event.setAssetId(asset.getId());
        event.setAssetCode(asset.getAssetCode());
        event.setEventType(eventType);
        event.setBeforeStatus(before.getCode());
        event.setAfterStatus(after.getCode());
        event.setBusinessRecordId(businessRecordId);
        event.setOperatorUserId(operatorUserId);
        event.setEventTime(LocalDateTime.now());
        assetEventMapper.insert(event);
    }
}
