package com.foxconn.iad.module.esd.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.foxconn.iad.module.esd.common.PageResponse;
import com.foxconn.iad.module.esd.controller.admin.returns.vo.ReturnCreateReqVO;
import com.foxconn.iad.module.esd.controller.admin.returns.vo.ReturnPageReqVO;
import com.foxconn.iad.module.esd.controller.admin.returns.vo.ReturnRespVO;
import com.foxconn.iad.module.esd.dal.dataobject.AssetDO;
import com.foxconn.iad.module.esd.dal.dataobject.AssetEventDO;
import com.foxconn.iad.module.esd.dal.dataobject.IssueRecordDO;
import com.foxconn.iad.module.esd.dal.dataobject.LaundryRecordDO;
import com.foxconn.iad.module.esd.dal.dataobject.ReturnRecordDO;
import com.foxconn.iad.module.esd.dal.mapper.AssetEventMapper;
import com.foxconn.iad.module.esd.dal.mapper.AssetMapper;
import com.foxconn.iad.module.esd.dal.mapper.IssueRecordMapper;
import com.foxconn.iad.module.esd.dal.mapper.LaundryRecordMapper;
import com.foxconn.iad.module.esd.dal.mapper.ReturnRecordMapper;
import com.foxconn.iad.module.esd.domain.AssetLifecycleStatus;
import com.foxconn.iad.module.esd.domain.BusinessRecordStatus;
import com.foxconn.iad.module.esd.domain.ReturnDisposition;
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
public class ReturnServiceImpl implements ReturnService {

    /** 資產主表 Mapper。 */
    private final AssetMapper assetMapper;
    /** 回收記錄 Mapper。 */
    private final ReturnRecordMapper returnRecordMapper;
    /** 發放記錄 Mapper，用於回收時結算。 */
    private final IssueRecordMapper issueRecordMapper;
    /** 清洗記錄 Mapper，送洗時建立進行中清洗記錄。 */
    private final LaundryRecordMapper laundryRecordMapper;
    /** 資產事件帳 Mapper。 */
    private final AssetEventMapper assetEventMapper;
    /** 廠區權限校驗。 */
    private final SiteAccessService siteAccessService;

    @Override
    @Transactional
    public Long create(ReturnCreateReqVO request) {
        LoginUserContext operator = siteAccessService.requireSite(request.getSiteCode());
        AssetDO asset = requireAssetByCode(request.getAssetCode(), request.getSiteCode());
        ReturnDisposition disposition = dispositionOf(request.getDisposition());
        // 目標狀態：直接入庫回到庫存，送洗進入待送洗。
        AssetLifecycleStatus target = disposition == ReturnDisposition.LAUNDER
                ? AssetLifecycleStatus.PENDING_LAUNDRY : AssetLifecycleStatus.AVAILABLE;
        // 條件更新：僅當資產仍為發放中且版本一致時才回收成功。
        LocalDateTime now = LocalDateTime.now();
        AssetDO patch = new AssetDO();
        patch.setLifecycleStatus(target.getCode());
        patch.setCurrentHolderUserId(null);
        patch.setCurrentHolderNo(null);
        patch.setCurrentHolderName(null);
        patch.setVersion(asset.getVersion() + 1);
        patch.setUpdater(String.valueOf(operator.getUserId()));
        patch.setUpdateTime(now);
        int updated = assetMapper.update(patch, new LambdaQueryWrapper<AssetDO>()
                .eq(AssetDO::getId, asset.getId())
                .eq(AssetDO::getSiteCode, request.getSiteCode())
                .eq(AssetDO::getLifecycleStatus, AssetLifecycleStatus.ISSUED.getCode())
                .eq(AssetDO::getVersion, asset.getVersion()));
        if (updated != 1) {
            throw new BusinessException(409, "資產狀態已改變，回收失敗");
        }
        // 結算進行中的發放記錄。
        IssueRecordDO issueRecord = settleActiveIssue(asset.getId(), request.getSiteCode(), now);
        // 寫入回收記錄。
        ReturnRecordDO record = new ReturnRecordDO();
        record.setRequestId(UUID.randomUUID().toString());
        record.setSiteCode(request.getSiteCode());
        record.setAssetId(asset.getId());
        record.setAssetCode(asset.getAssetCode());
        record.setAssetType(asset.getAssetType());
        record.setIssueRecordId(issueRecord == null ? null : issueRecord.getId());
        record.setEmployeeUserId(asset.getCurrentHolderUserId());
        record.setEmployeeNo(asset.getCurrentHolderNo());
        record.setEmployeeName(asset.getCurrentHolderName());
        record.setDisposition(disposition.getCode());
        record.setReturnerName(request.getReturnerName());
        record.setReceiverUserId(operator.getUserId());
        record.setReturnDate(request.getReturnDate());
        record.setRecordStatus(BusinessRecordStatus.COMPLETED.getCode());
        record.setCreator(String.valueOf(operator.getUserId()));
        record.setCreateTime(now);
        record.setUpdater(String.valueOf(operator.getUserId()));
        record.setUpdateTime(now);
        returnRecordMapper.insert(record);
        // 送洗時建立進行中的清洗記錄；送洗時間與操作人待送洗登記時才寫入。
        if (disposition == ReturnDisposition.LAUNDER) {
            LaundryRecordDO laundry = new LaundryRecordDO();
            laundry.setRequestId(UUID.randomUUID().toString());
            laundry.setSiteCode(request.getSiteCode());
            laundry.setAssetId(asset.getId());
            laundry.setAssetCode(asset.getAssetCode());
            laundry.setReturnRecordId(record.getId());
            laundry.setRecordStatus(BusinessRecordStatus.ACTIVE.getCode());
            laundry.setCreator(String.valueOf(operator.getUserId()));
            laundry.setCreateTime(now);
            laundry.setUpdater(String.valueOf(operator.getUserId()));
            laundry.setUpdateTime(now);
            laundryRecordMapper.insert(laundry);
        }
        writeEvent(request.getSiteCode(), asset, AssetLifecycleStatus.ISSUED, target,
                record.getId(), operator.getUserId(),
                disposition == ReturnDisposition.LAUNDER ? "RETURN_LAUNDER" : "RETURN_RESTOCK");
        return record.getId();
    }

    @Override
    public PageResponse<ReturnRespVO> page(ReturnPageReqVO request) {
        siteAccessService.requireSite(request.getSiteCode());
        LambdaQueryWrapper<ReturnRecordDO> query = new LambdaQueryWrapper<ReturnRecordDO>()
                .eq(ReturnRecordDO::getSiteCode, request.getSiteCode())
                .like(StringUtils.hasText(request.getKeyword()), ReturnRecordDO::getAssetCode, request.getKeyword())
                .orderByDesc(ReturnRecordDO::getCreateTime);
        IPage<ReturnRecordDO> page = returnRecordMapper.selectPage(
                new Page<>(request.getPageNo(), request.getPageSize()), query);
        List<ReturnRespVO> list = new ArrayList<>();
        for (ReturnRecordDO record : page.getRecords()) {
            list.add(toResponse(record));
        }
        return new PageResponse<>(list, page.getTotal());
    }

    /** 結算指定資產尚未歸還的發放記錄。 */
    private IssueRecordDO settleActiveIssue(Long assetId, String siteCode, LocalDateTime now) {
        IssueRecordDO active = issueRecordMapper.selectOne(new LambdaQueryWrapper<IssueRecordDO>()
                .eq(IssueRecordDO::getSiteCode, siteCode)
                .eq(IssueRecordDO::getAssetId, assetId)
                .eq(IssueRecordDO::getRecordStatus, BusinessRecordStatus.ACTIVE.getCode()));
        if (active == null) {
            return null;
        }
        active.setRecordStatus(BusinessRecordStatus.COMPLETED.getCode());
        active.setCloseTime(now);
        active.setUpdateTime(now);
        issueRecordMapper.updateById(active);
        return active;
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

    private ReturnDisposition dispositionOf(Integer code) {
        for (ReturnDisposition disposition : ReturnDisposition.values()) {
            if (disposition.getCode() == code) {
                return disposition;
            }
        }
        throw new BusinessException(400, "回收後續處置不合法");
    }

    /** 將回收記錄轉換成 API 響應。 */
    private ReturnRespVO toResponse(ReturnRecordDO record) {
        ReturnRespVO response = new ReturnRespVO();
        response.setId(record.getId());
        response.setAssetCode(record.getAssetCode());
        response.setAssetType(record.getAssetType());
        response.setEmployeeNo(record.getEmployeeNo());
        response.setEmployeeName(record.getEmployeeName());
        response.setReturnerName(record.getReturnerName());
        response.setReceiverName(record.getReceiverName());
        response.setReturnDate(record.getReturnDate());
        response.setDisposition(record.getDisposition());
        for (ReturnDisposition disposition : ReturnDisposition.values()) {
            if (disposition.getCode() == record.getDisposition()) {
                response.setDispositionName(disposition.getDisplayName());
                break;
            }
        }
        return response;
    }

    /** 追加資產事件帳。 */
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
