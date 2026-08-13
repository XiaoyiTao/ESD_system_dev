package com.foxconn.iad.module.esd.controller.admin.person.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class PersonProfileCreateReqVO {

    /** 人員綁定所屬廠區。 */
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
    /** 平台用戶 ID；姓名、工號和部門由平台 RPC 返回。 */
    /** 可選責任主管平台用戶 ID。 */
    /** 現場樓層屬性。 */
    /** 現場班別屬性。 */
