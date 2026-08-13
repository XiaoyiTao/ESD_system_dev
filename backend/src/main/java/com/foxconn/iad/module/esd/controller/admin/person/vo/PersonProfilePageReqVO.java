package com.foxconn.iad.module.esd.controller.admin.person.vo;

import com.foxconn.iad.module.esd.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
public class PersonProfilePageReqVO extends PageQuery {

    /** 查詢邊界由當前登錄用戶的廠區權限再次校驗。 */
    @NotBlank
    private String siteCode;
    /** 同時匹配工號和姓名。 */
    private String keyword;
    /** 平台部門 ID 過濾條件。 */
    private Long deptId;
    /** 1 啟用，0 停用；為空時查詢全部狀態。 */
    private Integer esdStatus;
}
