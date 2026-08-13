package com.foxconn.iad.esd.controller;

import com.foxconn.iad.esd.common.ApiResponse;
import com.foxconn.iad.esd.common.PageResponse;
import com.foxconn.iad.esd.controller.dto.person.PersonProfileCreateReq;
import com.foxconn.iad.esd.controller.dto.person.PersonProfilePageReq;
import com.foxconn.iad.esd.controller.dto.person.PersonProfileResp;
import com.foxconn.iad.esd.controller.dto.person.PersonProfileUpdateReq;
import com.foxconn.iad.esd.service.PersonProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/admin-api/esd/person-profiles")
@RequiredArgsConstructor
public class PersonProfileController {

    /** 人員擴展檔業務服務。 */
    private final PersonProfileService personProfileService;

    /** 從平台校驗用戶後創建 ESD 人員檔。 */
    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody PersonProfileCreateReq request) {
        return ApiResponse.success(personProfileService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id,
                                       @Valid @RequestBody PersonProfileUpdateReq request) {
        personProfileService.update(id, request);
        return ApiResponse.success(true);
    }

    @GetMapping
    public ApiResponse<PageResponse<PersonProfileResp>> page(@Valid PersonProfilePageReq request) {
        return ApiResponse.success(personProfileService.page(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<PersonProfileResp> get(@PathVariable Long id,
                                             @RequestParam String siteCode) {
        return ApiResponse.success(personProfileService.get(id, siteCode));
    }
}
    /** 更新主管、樓層、班別和啟停狀態。 */
    /** 按廠區分頁查詢人員檔。 */
    /** 查詢人員檔詳情及當前持有數量。 */
