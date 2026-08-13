package com.foxconn.iad.module.esd.controller.admin.issue.vo;

import com.foxconn.iad.module.esd.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
public class IssuePageReqVO extends PageQuery {

    /** 只允許查詢當前帳號有權訪問的廠區。 */
    @NotBlank
    private String siteCode;
    /** 匹配工號、姓名或物品編碼。 */
    private String keyword;
}
