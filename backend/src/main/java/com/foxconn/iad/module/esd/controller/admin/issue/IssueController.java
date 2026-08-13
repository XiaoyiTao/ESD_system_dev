package com.foxconn.iad.module.esd.controller.admin.issue;

import com.foxconn.iad.module.esd.common.ApiResponse;
import com.foxconn.iad.module.esd.common.PageResponse;
import com.foxconn.iad.module.esd.controller.admin.issue.vo.IssueCreateReqVO;
import com.foxconn.iad.module.esd.controller.admin.issue.vo.IssuePageReqVO;
import com.foxconn.iad.module.esd.controller.admin.issue.vo.IssueRespVO;
import com.foxconn.iad.module.esd.service.IssueService;
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
@RequestMapping("/admin-api/esd/issues")
@RequiredArgsConstructor
public class IssueController {

    /** 發放業務服務。 */
    private final IssueService issueService;

    /** 單筆發放。 */
    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody IssueCreateReqVO request) {
        return ApiResponse.success(issueService.create(request));
    }

    /** 按廠區分頁查詢發放記錄，一個資產多次發放全部顯示。 */
    @GetMapping("/page")
    public ApiResponse<PageResponse<IssueRespVO>> page(@Valid IssuePageReqVO request) {
        return ApiResponse.success(issueService.page(request));
    }
}
