package com.foxconn.iad.module.esd.controller.admin.asset;

import com.foxconn.iad.module.esd.common.ApiResponse;
import com.foxconn.iad.module.esd.common.PageResponse;
import com.foxconn.iad.module.esd.controller.admin.asset.vo.AssetCreateReqVO;
import com.foxconn.iad.module.esd.controller.admin.asset.vo.AssetPageReqVO;
import com.foxconn.iad.module.esd.controller.admin.asset.vo.AssetRespVO;
import com.foxconn.iad.module.esd.controller.admin.asset.vo.AssetUpdateReqVO;
import com.foxconn.iad.module.esd.service.AssetService;
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
    public ApiResponse<Long> create(@Valid @RequestBody AssetCreateReqVO request) {
        return ApiResponse.success(assetService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id,
                                       @Valid @RequestBody AssetUpdateReqVO request) {
        assetService.update(id, request);
        return ApiResponse.success(true);
    }

    @GetMapping
    public ApiResponse<PageResponse<AssetRespVO>> page(@Valid AssetPageReqVO request) {
        return ApiResponse.success(assetService.page(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<AssetRespVO> get(@PathVariable Long id,
                                     @RequestParam String siteCode) {
        return ApiResponse.success(assetService.get(id, siteCode));
    }
}
    /** 編輯顏色和尺碼，並要求客戶端帶回最新版本號。 */
    /** 按廠區和篩選條件分頁查詢資產。 */
    /** 查詢單件資產詳情，並再次校驗廠區權限。 */
