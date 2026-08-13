package com.foxconn.iad.module.esd.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.foxconn.iad.module.esd.common.PageResponse;
import com.foxconn.iad.module.esd.controller.admin.asset.vo.AssetCreateReqVO;
import com.foxconn.iad.module.esd.controller.admin.asset.vo.AssetPageReqVO;
import com.foxconn.iad.module.esd.controller.admin.asset.vo.AssetRespVO;
import com.foxconn.iad.module.esd.controller.admin.asset.vo.AssetUpdateReqVO;
import com.foxconn.iad.module.esd.dal.dataobject.AssetDO;
import com.foxconn.iad.module.esd.dal.dataobject.IssueRecordDO;
import com.foxconn.iad.module.esd.dal.dataobject.LaundryRecordDO;
import com.foxconn.iad.module.esd.dal.mapper.AssetMapper;
import com.foxconn.iad.module.esd.dal.mapper.IssueRecordMapper;
import com.foxconn.iad.module.esd.dal.mapper.LaundryRecordMapper;
import com.foxconn.iad.module.esd.domain.AssetLifecycleStatus;
import com.foxconn.iad.module.esd.exception.BusinessException;
import com.foxconn.iad.module.esd.security.LoginUserContext;
import com.foxconn.iad.module.esd.security.SiteAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    /** 資產主表 Mapper。 */
    private final AssetMapper assetMapper;
    /** 發放記錄 Mapper，用於查詢最近一次發放。 */
    private final IssueRecordMapper issueRecordMapper;
    /** 清洗記錄 Mapper，用於查詢最近一次清洗。 */
    private final LaundryRecordMapper laundryRecordMapper;
    /** 統一登錄廠區權限校驗器。 */
    private final SiteAccessService siteAccessService;

    @Override
    @Transactional
    public Long create(AssetCreateReqVO request) {
        LoginUserContext operator = siteAccessService.requireSite(request.getSiteCode());
        // 插入前先校驗廠區，避免客戶端偽造 siteCode 寫入其他廠區的數據。
        AssetDO asset = new AssetDO();
        asset.setAssetCode(request.getAssetCode().trim());
        asset.setSiteCode(request.getSiteCode().trim());
        asset.setAssetType(request.getAssetType());
        asset.setColorCode(request.getColorCode().trim());
        asset.setSizeCode(request.getSizeCode().trim());
        asset.setLifecycleStatus(AssetLifecycleStatus.AVAILABLE.getCode());
        asset.setCleanCount(0);
        // 版本從 0 開始，後續編輯和交易都通過版本條件防止覆蓋並發修改。
        asset.setVersion(0);
        asset.setCreator(String.valueOf(operator.getUserId()));
        asset.setUpdater(String.valueOf(operator.getUserId()));
        asset.setCreateTime(LocalDateTime.now());
        asset.setUpdateTime(LocalDateTime.now());
        try {
            assetMapper.insert(asset);
        } catch (DuplicateKeyException exception) {
            throw new BusinessException(400, "資產編碼已存在：" + request.getAssetCode());
        }
        return asset.getId();
    }

    @Override
    @Transactional
    public void update(Long id, AssetUpdateReqVO request) {
        LoginUserContext operator = siteAccessService.requireSite(request.getSiteCode());
        AssetDO asset = requireAsset(id, request.getSiteCode());
        // 先讀版本可以快速返回明確錯誤；最終 UPDATE 的 version 條件仍是並發安全的關鍵。
        if (request.getVersion() == null || !request.getVersion().equals(asset.getVersion())) {
            throw new BusinessException(409, "資產已被其他操作更新，請刷新後重試");
        }
        asset.setColorCode(request.getColorCode().trim());
        asset.setSizeCode(request.getSizeCode().trim());
        asset.setVersion(asset.getVersion() + 1);
        asset.setUpdater(String.valueOf(operator.getUserId()));
        asset.setUpdateTime(LocalDateTime.now());
        int updated = assetMapper.update(asset, new LambdaQueryWrapper<AssetDO>()
                .eq(AssetDO::getId, id)
                .eq(AssetDO::getSiteCode, request.getSiteCode())
                .eq(AssetDO::getVersion, request.getVersion()));
        if (updated != 1) {
            throw new BusinessException(409, "資產已被其他操作更新，請刷新後重試");
        }
    }

    @Override
    public PageResponse<AssetRespVO> page(AssetPageReqVO request) {
        siteAccessService.requireSite(request.getSiteCode());
        // 廠區條件始終作為 AND 條件，防止關鍵字 OR 子句突破數據隔離邊界。
        LambdaQueryWrapper<AssetDO> query = new LambdaQueryWrapper<AssetDO>()
                .eq(AssetDO::getSiteCode, request.getSiteCode())
                .eq(request.getAssetType() != null, AssetDO::getAssetType, request.getAssetType())
                .eq(request.getLifecycleStatus() != null, AssetDO::getLifecycleStatus, request.getLifecycleStatus())
                .eq(StringUtils.hasText(request.getColorCode()), AssetDO::getColorCode, request.getColorCode())
                .eq(StringUtils.hasText(request.getSizeCode()), AssetDO::getSizeCode, request.getSizeCode())
                .and(StringUtils.hasText(request.getKeyword()), wrapper -> wrapper
                        .like(AssetDO::getAssetCode, request.getKeyword())
                        .or().like(AssetDO::getCurrentHolderNo, request.getKeyword())
                        .or().like(AssetDO::getCurrentHolderName, request.getKeyword()))
                .orderByDesc(AssetDO::getCreateTime);
        IPage<AssetDO> page = assetMapper.selectPage(new Page<>(request.getPageNo(), request.getPageSize()), query);
        List<AssetRespVO> list = new ArrayList<>();
        for (AssetDO asset : page.getRecords()) {
            list.add(toResponse(asset));
        }
        return new PageResponse<>(list, page.getTotal());
    }

    @Override
    public AssetRespVO get(Long id, String siteCode) {
        siteAccessService.requireSite(siteCode);
        AssetDO asset = requireAsset(id, siteCode);
        AssetRespVO response = toResponse(asset);
        // 發放歷史與清洗歷史只顯示最近一次，避免詳情過載。
        response.setLatestIssue(latestIssue(asset.getId(), siteCode));
        response.setLatestLaundry(latestLaundry(asset.getId(), siteCode));
        return response;
    }

    /** 查詢資產最近一次發放記錄，無發放歷史返回空。 */
    private AssetRespVO.LatestIssueVO latestIssue(Long assetId, String siteCode) {
        IssueRecordDO record = issueRecordMapper.selectOne(new LambdaQueryWrapper<IssueRecordDO>()
                .eq(IssueRecordDO::getSiteCode, siteCode)
                .eq(IssueRecordDO::getAssetId, assetId)
                .orderByDesc(IssueRecordDO::getCreateTime)
                .last("LIMIT 1"));
        if (record == null) {
            return null;
        }
        AssetRespVO.LatestIssueVO latest = new AssetRespVO.LatestIssueVO();
        latest.setIssueDate(record.getIssueDate());
        latest.setEmployeeNo(record.getEmployeeNo());
        latest.setEmployeeName(record.getEmployeeName());
        latest.setIssueOperatorName(record.getIssueOperatorName());
        latest.setReturnDate(record.getCloseTime() == null ? null
                : LocalDate.from(record.getCloseTime()));
        return latest;
    }

    /** 查詢資產最近一次清洗記錄，無清洗歷史返回空。 */
    private AssetRespVO.LatestLaundryVO latestLaundry(Long assetId, String siteCode) {
        LaundryRecordDO record = laundryRecordMapper.selectOne(new LambdaQueryWrapper<LaundryRecordDO>()
                .eq(LaundryRecordDO::getSiteCode, siteCode)
                .eq(LaundryRecordDO::getAssetId, assetId)
                .orderByDesc(LaundryRecordDO::getCreateTime)
                .last("LIMIT 1"));
        if (record == null) {
            return null;
        }
        AssetRespVO.LatestLaundryVO latest = new AssetRespVO.LatestLaundryVO();
        latest.setSendTime(record.getSendTime());
        latest.setCompleteTime(record.getCompleteTime());
        return latest;
    }

    /** 在指定廠區內查找資產，不向調用方暴露其他廠區同 ID 的數據。 */
    private AssetDO requireAsset(Long id, String siteCode) {
        AssetDO asset = assetMapper.selectOne(new LambdaQueryWrapper<AssetDO>()
                .eq(AssetDO::getId, id)
                .eq(AssetDO::getSiteCode, siteCode));
        if (asset == null) {
            throw new BusinessException(404, "資產不存在");
        }
        return asset;
    }

    /** 將資料庫對象轉換成穩定的 API 響應，並補充狀態中文名稱。 */
    private AssetRespVO toResponse(AssetDO asset) {
        AssetRespVO response = new AssetRespVO();
        response.setId(asset.getId());
        response.setAssetCode(asset.getAssetCode());
        response.setSiteCode(asset.getSiteCode());
        response.setAssetType(asset.getAssetType());
        response.setColorCode(asset.getColorCode());
        response.setSizeCode(asset.getSizeCode());
        response.setLifecycleStatus(asset.getLifecycleStatus());
        // 資料庫狀態受 CHECK 約束；循環轉換避免 API 層直接暴露枚舉實現細節。
        for (AssetLifecycleStatus status : AssetLifecycleStatus.values()) {
            if (status.getCode() == asset.getLifecycleStatus()) {
                response.setLifecycleStatusName(status.getDisplayName());
                break;
            }
        }
        response.setCurrentHolderUserId(asset.getCurrentHolderUserId());
        response.setCurrentHolderNo(asset.getCurrentHolderNo());
        response.setCurrentHolderName(asset.getCurrentHolderName());
        response.setCleanCount(asset.getCleanCount());
        response.setVersion(asset.getVersion());
        response.setCreateTime(asset.getCreateTime());
        response.setUpdateTime(asset.getUpdateTime());
        return response;
    }
}
