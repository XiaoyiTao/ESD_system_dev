package com.foxconn.iad.module.esd.controller.admin.returns;

import com.foxconn.iad.module.esd.common.ApiResponse;
import com.foxconn.iad.module.esd.common.PageResponse;
import com.foxconn.iad.module.esd.controller.admin.returns.vo.ReturnCreateReqVO;
import com.foxconn.iad.module.esd.controller.admin.returns.vo.ReturnPageReqVO;
import com.foxconn.iad.module.esd.controller.admin.returns.vo.ReturnRespVO;
import com.foxconn.iad.module.esd.service.ReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/admin-api/esd/returns")
@RequiredArgsConstructor
public class ReturnController {

    /** 回收業務服務。 */
    private final ReturnService returnService;

    /** 單筆回收：直接入庫或送洗。 */
    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody ReturnCreateReqVO request) {
        return ApiResponse.success(returnService.create(request));
    }

    /** 按廠區分頁查詢回收記錄。 */
    @GetMapping("/page")
    public ApiResponse<PageResponse<ReturnRespVO>> page(@Valid ReturnPageReqVO request) {
        return ApiResponse.success(returnService.page(request));
    }
}
