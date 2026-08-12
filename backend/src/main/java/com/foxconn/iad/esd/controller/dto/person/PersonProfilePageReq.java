package com.foxconn.iad.esd.controller.dto.person;

import com.foxconn.iad.esd.controller.dto.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
public class PersonProfilePageReq extends PageQuery {

    /** 查询边界由当前登录用户的厂区权限再次校验。 */
    @NotBlank
    private String siteCode;
    /** 同时匹配工号和姓名。 */
    private String keyword;
    /** 平台部门 ID 过滤条件。 */
    private Long deptId;
    /** 1 启用，0 停用；为空时查询全部状态。 */
    private Integer esdStatus;
}
