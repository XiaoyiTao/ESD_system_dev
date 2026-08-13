package com.foxconn.iad.module.esd.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.foxconn.iad.module.esd.controller.admin.laundry.vo.LaundryRespVO;
import com.foxconn.iad.module.esd.dal.dataobject.AssetDO;
import com.foxconn.iad.module.esd.dal.dataobject.AssetEventDO;
import com.foxconn.iad.module.esd.dal.dataobject.LaundryRecordDO;
import com.foxconn.iad.module.esd.dal.mapper.AssetEventMapper;
import com.foxconn.iad.module.esd.dal.mapper.AssetMapper;
import com.foxconn.iad.module.esd.dal.mapper.LaundryRecordMapper;
import com.foxconn.iad.module.esd.domain.AssetLifecycleStatus;
import com.foxconn.iad.module.esd.domain.BusinessRecordStatus;
import com.foxconn.iad.module.esd.exception.BusinessException;
import com.foxconn.iad.module.esd.security.LoginUserContext;
import com.foxconn.iad.module.esd.security.SiteAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LaundryServiceImpl implements LaundryService {

    /** 資產主表 Mapper。 */
    private final AssetMapper assetMapper;
    /** 清洗記錄 Mapper。 */
    private final LaundryRecordMapper laundryRecordMapper;
    /** 資產事件帳 Mapper。 */
    private final AssetEventMapper assetEventMapper;
    /** 廠區權限校驗。 */
    private final SiteAccessService siteAccessService;

    @Override
    public List<LaundryRespVO> activePage(String siteCode) {
        siteAccessService.requireSite(siteCode);
        // 進行中的清洗記錄，聯表資產取得類型、顏色、尺碼與當前狀態。
        List<LaundryRecordDO> records = laundryRecordMapper.selectList(new LambdaQueryWrapper<LaundryRecordDO>()
                .eq(LaundryRecordDO::getSiteCode, siteCode)
                .eq(LaundryRecordDO::getRecordStatus, BusinessRecordStatus.ACTIVE.getCode())
                .orderByAsc(LaundryRecordDO::getCreateTime));
        if (records.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> assetIds = records.stream().map(LaundryRecordDO::getAssetId).collect(Collectors.toList());
        Map<Long, AssetDO> assetMap = new HashMap<>();
        for (AssetDO asset : assetMapper.selectBatchIds(assetIds)) {
            assetMap.put(asset.getId(), asset);
        }
        List<LaundryRespVO> list = new ArrayList<>();
        for (LaundryRecordDO record : records) {
            AssetDO asset = assetMap.get(record.getAssetId());
            if (asset == null) {
                continue;
            }
            list.add(toResponse(record, asset));
        }
        return list;
    }

    @Override
    @Transactional
    public void start(Long id, String siteCode) {
        LoginUserContext operator = siteAccessService.requireSite(siteCode);
        LaundryRecordDO record = requireActiveLaundry(id, siteCode);
        AssetDO asset = requireAsset(record.getAssetId(), siteCode);
        // 條件更新：僅當資產仍為待送洗且版本一致時才推進為清洗中。
        LocalDateTime now = LocalDateTime.now();
        AssetDO patch = new AssetDO();
        patch.setLifecycleStatus(AssetLifecycleStatus.IN_LAUNDRY.getCode());
        patch.setVersion(asset.getVersion() + 1);
        patch.setUpdater(String.valueOf(operator.getUserId()));
        patch.setUpdateTime(now);
        int updated = assetMapper.update(patch, new LambdaQueryWrapper<AssetDO>()
                .eq(AssetDO::getId, asset.getId())
                .eq(AssetDO::getSiteCode, siteCode)
                .eq(AssetDO::getLifecycleStatus, AssetLifecycleStatus.PENDING_LAUNDRY.getCode())
                .eq(AssetDO::getVersion, asset.getVersion()));
        if (updated != 1) {
            throw new BusinessException(409, "資產狀態已改變，送洗登記失敗");
        }
        // 送洗登記寫入送洗時間與操作人。
        record.setSendOperatorUserId(operator.getUserId());
        record.setSendTime(now);
        record.setUpdateTime(now);
        laundryRecordMapper.updateById(record);
        writeEvent(siteCode, asset, AssetLifecycleStatus.PENDING_LAUNDRY, AssetLifecycleStatus.IN_LAUNDRY,
                record.getId(), operator.getUserId(), "LAUNDER_START");
    }

    @Override
    @Transactional
    public void complete(Long id, String siteCode) {
        LoginUserContext operator = siteAccessService.requireSite(siteCode);
        LaundryRecordDO record = requireActiveLaundry(id, siteCode);
        AssetDO asset = requireAsset(record.getAssetId(), siteCode);
        // 條件更新：僅當資產仍為清洗中且版本一致時才完成清洗，並累加清洗次數。
        LocalDateTime now = LocalDateTime.now();
        AssetDO patch = new AssetDO();
        patch.setLifecycleStatus(AssetLifecycleStatus.AVAILABLE.getCode());
        patch.setCleanCount(asset.getCleanCount() + 1);
        patch.setVersion(asset.getVersion() + 1);
        patch.setUpdater(String.valueOf(operator.getUserId()));
        patch.setUpdateTime(now);
        int updated = assetMapper.update(patch, new LambdaQueryWrapper<AssetDO>()
                .eq(AssetDO::getId, asset.getId())
                .eq(AssetDO::getSiteCode, siteCode)
                .eq(AssetDO::getLifecycleStatus, AssetLifecycleStatus.IN_LAUNDRY.getCode())
                .eq(AssetDO::getVersion, asset.getVersion()));
        if (updated != 1) {
            throw new BusinessException(409, "資產狀態已改變，完成清洗失敗");
        }
        // 完成清洗記錄。
        record.setCompleteOperatorUserId(operator.getUserId());
        record.setCompleteTime(now);
        record.setRecordStatus(BusinessRecordStatus.COMPLETED.getCode());
        record.setUpdateTime(now);
        laundryRecordMapper.updateById(record);
        writeEvent(siteCode, asset, AssetLifecycleStatus.IN_LAUNDRY, AssetLifecycleStatus.AVAILABLE,
                record.getId(), operator.getUserId(), "LAUNDER_COMPLETE");
    }

    /** 查找指定廠區內進行中的清洗記錄。 */
    private LaundryRecordDO requireActiveLaundry(Long id, String siteCode) {
        LaundryRecordDO record = laundryRecordMapper.selectOne(new LambdaQueryWrapper<LaundryRecordDO>()
                .eq(LaundryRecordDO::getId, id)
                .eq(LaundryRecordDO::getSiteCode, siteCode)
                .eq(LaundryRecordDO::getRecordStatus, BusinessRecordStatus.ACTIVE.getCode()));
        if (record == null) {
            throw new BusinessException(404, "清洗記錄不存在或已完成");
        }
        return record;
    }

    /** 按 ID 查找指定廠區內的資產。 */
    private AssetDO requireAsset(Long assetId, String siteCode) {
        AssetDO asset = assetMapper.selectOne(new LambdaQueryWrapper<AssetDO>()
                .eq(AssetDO::getId, assetId)
                .eq(AssetDO::getSiteCode, siteCode));
        if (asset == null) {
            throw new BusinessException(404, "資產不存在");
        }
        return asset;
    }

    /** 將清洗記錄與資產組裝成 API 響應。 */
    private LaundryRespVO toResponse(LaundryRecordDO record, AssetDO asset) {
        LaundryRespVO response = new LaundryRespVO();
        response.setId(record.getId());
        response.setAssetCode(asset.getAssetCode());
        response.setAssetType(asset.getAssetType());
        response.setColorCode(asset.getColorCode());
        response.setSizeCode(asset.getSizeCode());
        response.setLifecycleStatus(asset.getLifecycleStatus());
        for (AssetLifecycleStatus status : AssetLifecycleStatus.values()) {
            if (status.getCode() == asset.getLifecycleStatus()) {
                response.setLifecycleStatusName(status.getDisplayName());
                break;
            }
        }
        response.setSendTime(record.getSendTime());
        response.setSendOperatorName(record.getSendOperatorName());
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
