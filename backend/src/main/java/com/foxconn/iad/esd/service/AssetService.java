package com.foxconn.iad.esd.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.foxconn.iad.esd.common.PageResponse;
import com.foxconn.iad.esd.controller.dto.asset.AssetCreateReq;
import com.foxconn.iad.esd.controller.dto.asset.AssetPageReq;
import com.foxconn.iad.esd.controller.dto.asset.AssetResp;
import com.foxconn.iad.esd.controller.dto.asset.AssetUpdateReq;
import com.foxconn.iad.esd.dal.dataobject.AssetDO;
import com.foxconn.iad.esd.dal.mapper.AssetMapper;
import com.foxconn.iad.esd.exception.BusinessException;
import com.foxconn.iad.esd.security.LoginUserContext;
import com.foxconn.iad.esd.security.SiteAccessService;
import com.foxconn.iad.esd.domain.AssetLifecycleStatus;
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
public class AssetService {

    private final AssetMapper assetMapper;
    private final SiteAccessService siteAccessService;

    @Transactional
    public Long create(AssetCreateReq request) {
        LoginUserContext operator = siteAccessService.requireSite(request.getSiteCode());
        AssetDO asset = new AssetDO();
        asset.setAssetCode(request.getAssetCode().trim());
        asset.setSiteCode(request.getSiteCode().trim());
        asset.setAssetType(request.getAssetType());
        asset.setColorCode(request.getColorCode().trim());
        asset.setSizeCode(request.getSizeCode().trim());
        asset.setLifecycleStatus(AssetLifecycleStatus.AVAILABLE.getCode());
        asset.setCleanCount(0);
        asset.setVersion(0);
        asset.setCreator(String.valueOf(operator.getUserId()));
        asset.setUpdater(String.valueOf(operator.getUserId()));
        asset.setCreateTime(LocalDateTime.now());
        asset.setUpdateTime(LocalDateTime.now());
        try {
            assetMapper.insert(asset);
        } catch (DuplicateKeyException exception) {
            throw new BusinessException(400, "资产编码已存在：" + request.getAssetCode());
        }
        return asset.getId();
    }

    @Transactional
    public void update(Long id, AssetUpdateReq request) {
        LoginUserContext operator = siteAccessService.requireSite(request.getSiteCode());
        AssetDO asset = requireAsset(id, request.getSiteCode());
        if (request.getVersion() == null || !request.getVersion().equals(asset.getVersion())) {
            throw new BusinessException(409, "资产已被其他操作更新，请刷新后重试");
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
            throw new BusinessException(409, "资产已被其他操作更新，请刷新后重试");
        }
    }

    public PageResponse<AssetResp> page(AssetPageReq request) {
        siteAccessService.requireSite(request.getSiteCode());
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
        List<AssetResp> list = new ArrayList<>();
        for (AssetDO asset : page.getRecords()) {
            list.add(toResponse(asset));
        }
        return new PageResponse<>(list, page.getTotal());
    }

    public AssetResp get(Long id, String siteCode) {
        siteAccessService.requireSite(siteCode);
        return toResponse(requireAsset(id, siteCode));
    }

    private AssetDO requireAsset(Long id, String siteCode) {
        AssetDO asset = assetMapper.selectOne(new LambdaQueryWrapper<AssetDO>()
                .eq(AssetDO::getId, id)
                .eq(AssetDO::getSiteCode, siteCode));
        if (asset == null) {
            throw new BusinessException(404, "资产不存在");
        }
        return asset;
    }

    private AssetResp toResponse(AssetDO asset) {
        AssetResp response = new AssetResp();
        response.setId(asset.getId());
        response.setAssetCode(asset.getAssetCode());
        response.setSiteCode(asset.getSiteCode());
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

