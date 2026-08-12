package com.foxconn.iad.esd.controller.dto.person;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class PersonProfileCreateReq {

    /** 人员绑定所属厂区。 */
    @NotBlank
    @Size(max = 32)
    private String siteCode;

    @NotNull
    private Long platformUserId;

    private Long supervisorUserId;

    @Size(max = 32)
    private String floorCode;

    @Size(max = 32)
    private String shiftCode;
}
    /** 平台用户 ID；姓名、工号和部门由平台 RPC 返回。 */
    /** 可选责任主管平台用户 ID。 */
    /** 现场楼层属性。 */
    /** 现场班别属性。 */
