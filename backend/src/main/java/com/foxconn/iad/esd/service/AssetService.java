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

    /** 资产主表 Mapper。 */
    private final AssetMapper assetMapper;
    /** 统一登录厂区权限校验器。 */
    private final SiteAccessService siteAccessService;

    /**
     * 创建资产主档。
     *
     * <p>所有新资产从可用库存开始，发放、清洗和终止状态只能由后续业务交易改变。</p>
     */
    @Transactional
    public Long create(AssetCreateReq request) {
        LoginUserContext operator = siteAccessService.requireSite(request.getSiteCode());
        // 插入前先校验厂区，避免客户端伪造 siteCode 写入其他厂区的数据。
        AssetDO asset = new AssetDO();
        asset.setAssetCode(request.getAssetCode().trim());
        asset.setSiteCode(request.getSiteCode().trim());
        asset.setAssetType(request.getAssetType());
        asset.setColorCode(request.getColorCode().trim());
        asset.setSizeCode(request.getSizeCode().trim());
        asset.setLifecycleStatus(AssetLifecycleStatus.AVAILABLE.getCode());
        asset.setCleanCount(0);
        // 版本从 0 开始，后续编辑和交易都通过版本条件防止覆盖并发修改。
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
        // 先读版本可以快速返回明确错误；最终 UPDATE 的 version 条件仍是并发安全的关键。
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
        // 厂区条件始终作为 AND 条件，防止关键字 OR 子句突破数据隔离边界。
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
        // 数据库状态受 CHECK 约束；循环转换避免 API 层直接暴露枚举实现细节。
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
    /**
     * 编辑资产的基础属性。
     *
     * <p>使用“查询版本 + UPDATE ... WHERE version = 旧版本”的 CAS 方式，
     * 因此两个页面同时编辑时只有一个请求可以成功。</p>
     */
    /** 按厂区分页查询资产，关键字同时搜索编码和当前持有人快照。 */
    /** 查询单件资产详情。 */
    /** 在指定厂区内查找资产，不向调用方暴露其他厂区同 ID 的数据。 */
    /** 将数据库对象转换成稳定的 API 响应，并补充状态中文名称。 */
