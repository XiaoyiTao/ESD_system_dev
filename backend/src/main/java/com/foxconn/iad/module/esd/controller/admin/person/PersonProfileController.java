package com.foxconn.iad.module.esd.controller.admin.person;

import com.foxconn.iad.module.esd.common.CommonResult;
import com.foxconn.iad.module.esd.common.PageResult;
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
    public CommonResult<Long> create(@Valid @RequestBody PersonProfileCreateReqVO request) {
        return CommonResult.success(personProfileService.create(request));
    }

    @PutMapping("/{id}")
    public CommonResult<Boolean> update(@PathVariable Long id,
                                       @Valid @RequestBody PersonProfileUpdateReqVO request) {
        personProfileService.update(id, request);
        return CommonResult.success(true);
    }

    @GetMapping
    public CommonResult<PageResult<PersonProfileRespVO>> page(@Valid PersonProfilePageReqVO request) {
        return CommonResult.success(personProfileService.page(request));
    }

    @GetMapping("/{id}")
    public CommonResult<PersonProfileRespVO> get(@PathVariable Long id,
                                             @RequestParam String siteCode) {
        return CommonResult.success(personProfileService.get(id, siteCode));
    }
}
    /** 更新主管、樓層、班別和啟停狀態。 */
    /** 按廠區分頁查詢人員檔。 */
    /** 查詢人員檔詳情及當前持有數量。 */
