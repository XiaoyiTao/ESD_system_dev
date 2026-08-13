package com.foxconn.iad.module.esd.controller.dto.person;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class PersonProfileUpdateReqVO {

    /** 用於確認記錄所屬廠區並執行數據隔離。 */
    @NotBlank
    @Size(max = 32)
    private String siteCode;
    /** 可選責任主管，必須來自同一廠區且處於啟用狀態。 */
    private Long supervisorUserId;
    /** 現場樓層屬性。 */
    @Size(max = 32)
    private String floorCode;
    /** 現場班別屬性。 */
    @Size(max = 32)
    private String shiftCode;
    /** 1 啟用，0 停用；停用前必須確認沒有持有資產。 */
    @Min(0)
    @Max(1)
    private Integer esdStatus;
}
