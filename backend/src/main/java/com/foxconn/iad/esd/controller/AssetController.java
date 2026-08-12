package com.foxconn.iad.esd.controller;

import com.foxconn.iad.esd.common.ApiResponse;
import com.foxconn.iad.esd.common.PageResponse;
import com.foxconn.iad.esd.controller.dto.asset.AssetCreateReq;
import com.foxconn.iad.esd.controller.dto.asset.AssetPageReq;
import com.foxconn.iad.esd.controller.dto.asset.AssetResp;
import com.foxconn.iad.esd.controller.dto.asset.AssetUpdateReq;
import com.foxconn.iad.esd.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/admin-api/esd/assets")
@RequiredArgsConstructor
public class AssetController {

    /** 资产主档业务服务。 */
    private final AssetService assetService;

    /** 新增一件可用库存资产。 */
    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody AssetCreateReq request) {
        return ApiResponse.success(assetService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id,
                                       @Valid @RequestBody AssetUpdateReq request) {
        assetService.update(id, request);
        return ApiResponse.success(true);
    }

    @GetMapping
    public ApiResponse<PageResponse<AssetResp>> page(@Valid AssetPageReq request) {
        return ApiResponse.success(assetService.page(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<AssetResp> get(@PathVariable Long id,
                                     @RequestParam String siteCode) {
        return ApiResponse.success(assetService.get(id, siteCode));
    }
}
    /** 编辑颜色和尺码，并要求客户端带回最新版本号。 */
    /** 按厂区和筛选条件分页查询资产。 */
    /** 查询单件资产详情，并再次校验厂区权限。 */
