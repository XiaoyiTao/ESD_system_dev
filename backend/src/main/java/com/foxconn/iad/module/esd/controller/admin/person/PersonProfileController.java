package com.foxconn.iad.module.esd.controller.admin.person;

import com.foxconn.iad.module.esd.common.ApiResponse;
import com.foxconn.iad.module.esd.common.PageResponse;
import com.foxconn.iad.module.esd.controller.admin.person.vo.PersonProfileCreateReqVO;
import com.foxconn.iad.module.esd.controller.admin.person.vo.PersonProfilePageReqVO;
import com.foxconn.iad.module.esd.controller.admin.person.vo.PersonProfileRespVO;
import com.foxconn.iad.module.esd.controller.admin.person.vo.PersonProfileUpdateReqVO;
import com.foxconn.iad.module.esd.service.PersonProfileService;
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
    public ApiResponse<Long> create(@Valid @RequestBody PersonProfileCreateReqVO request) {
        return ApiResponse.success(personProfileService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id,
                                       @Valid @RequestBody PersonProfileUpdateReqVO request) {
        personProfileService.update(id, request);
        return ApiResponse.success(true);
    }

    @GetMapping
    public ApiResponse<PageResponse<PersonProfileRespVO>> page(@Valid PersonProfilePageReqVO request) {
        return ApiResponse.success(personProfileService.page(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<PersonProfileRespVO> get(@PathVariable Long id,
                                             @RequestParam String siteCode) {
        return ApiResponse.success(personProfileService.get(id, siteCode));
    }
}
    /** 更新主管、樓層、班別和啟停狀態。 */
    /** 按廠區分頁查詢人員檔。 */
    /** 查詢人員檔詳情及當前持有數量。 */
