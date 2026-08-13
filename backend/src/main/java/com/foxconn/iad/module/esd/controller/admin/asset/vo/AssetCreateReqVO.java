package com.foxconn.iad.module.esd.controller.admin.asset.vo;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class AssetCreateReqVO {

    /** 資產編碼，只允許便於標簽打印和掃描的 ASCII 字符。 */
    @NotBlank
    @Size(max = 64)
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "資產編碼只能包含字母、數字、下劃線和連字符")
    private String assetCode;
    @NotBlank
    @Size(max = 32)
    private String siteCode;
    @NotNull
    @Min(1)
    @Max(2)
    private Integer assetType;
    @NotBlank
    @Size(max = 32)
    private String colorCode;
    @NotBlank
    @Size(max = 32)
    private String sizeCode;
}
    /** 入庫所屬廠區；服務端會與當前登錄用戶的廠區權限比對。 */
    /** 1 靜電衣，2 靜電鞋。 */
    /** 顏色編碼或名稱。 */
    /** 衣服尺碼或鞋子尺碼。 */
