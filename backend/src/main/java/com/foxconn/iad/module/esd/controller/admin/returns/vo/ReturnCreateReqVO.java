package com.foxconn.iad.module.esd.controller.admin.returns.vo;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class ReturnCreateReqVO {

    /** 回收所屬廠區，服務端會與當前登錄用戶的廠區權限比對。 */
    @NotBlank
    @Size(max = 32)
    private String siteCode;
    /** 物品編碼，僅發放中狀態可回收。 */
    @NotBlank
    @Size(max = 64)
    private String assetCode;
    /** 後續處置：1 直接入庫，2 送洗。 */
    @NotNull
    @Min(1)
    @Max(2)
    private Integer disposition;
    /** 歸還人姓名，可空。 */
    @Size(max = 100)
    private String returnerName;
    /** 回收日期。 */
    @NotNull
    private LocalDate returnDate;
}
