package com.foxconn.iad.module.esd.controller.admin.issue.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class IssueCreateReqVO {

    /** 發放所屬廠區，服務端會與當前登錄用戶的廠區權限比對。 */
    @NotBlank
    @Size(max = 32)
    private String siteCode;
    /** 領用員工平台用戶 ID。 */
    @NotNull
    private Long employeeUserId;
    /** 物品編碼，僅庫存狀態可發放。 */
    @NotBlank
    @Size(max = 64)
    private String assetCode;
    /** 發放日期。 */
    @NotNull
    private LocalDate issueDate;
    /** 備註，可空。 */
    @Size(max = 200)
    private String remark;
}
