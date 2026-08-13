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

    /** 資產主檔業務服務。 */
    private final AssetService assetService;

    /** 新增一件可用庫存資產。 */
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
    /** 編輯顏色和尺碼，並要求客戶端帶回最新版本號。 */
    /** 按廠區和篩選條件分頁查詢資產。 */
    /** 查詢單件資產詳情，並再次校驗廠區權限。 */
