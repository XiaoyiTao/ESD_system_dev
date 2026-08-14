package com.foxconn.iad.module.esd.controller.admin.laundry;

import com.foxconn.iad.module.esd.common.CommonResult;
import com.foxconn.iad.module.esd.controller.admin.laundry.vo.LaundryRespVO;
import com.foxconn.iad.module.esd.service.LaundryService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/admin-api/esd/laundries")
@RequiredArgsConstructor
public class LaundryController {

    /** 清洗業務服務。 */
    private final LaundryService laundryService;

    /** 查詢待送洗與清洗中的進行中清洗列表。 */
    @GetMapping("/active-page")
    public CommonResult<List<LaundryRespVO>> activePage(@RequestParam String siteCode) {
        return CommonResult.success(laundryService.activePage(siteCode));
    }

    /** 送洗登記：待送洗 -> 清洗中。 */
    @PutMapping("/{id}/start")
    public CommonResult<Boolean> start(@PathVariable Long id, @RequestParam String siteCode) {
        laundryService.start(id, siteCode);
        return CommonResult.success(true);
    }

    /** 完成清洗：清洗中 -> 庫存，清洗次數 +1。 */
    @PutMapping("/{id}/complete")
    public CommonResult<Boolean> complete(@PathVariable Long id, @RequestParam String siteCode) {
        laundryService.complete(id, siteCode);
        return CommonResult.success(true);
    }
}
