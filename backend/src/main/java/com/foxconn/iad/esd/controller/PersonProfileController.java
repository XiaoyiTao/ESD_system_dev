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

    /** 人员扩展档业务服务。 */
    private final PersonProfileService personProfileService;

    /** 从平台校验用户后创建 ESD 人员档。 */
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
    /** 更新主管、楼层、班别和启停状态。 */
    /** 按厂区分页查询人员档。 */
    /** 查询人员档详情及当前持有数量。 */
