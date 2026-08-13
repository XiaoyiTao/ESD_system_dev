package com.foxconn.iad.module.esd.controller.dto.asset;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AssetUpdateReqVO {

    /** 資產所屬廠區，同時作為更新 SQL 的數據隔離條件。 */
    @NotBlank
    @Size(max = 32)
    private String siteCode;
    @NotBlank
    @Size(max = 32)
    private String colorCode;
    @NotBlank
    @Size(max = 32)
    private String sizeCode;

    @NotNull
    private Integer version;
}
    /** 可編輯顏色。資產類型和編碼入庫後不可在此接口修改。 */
    /** 可編輯尺碼。 */
    /** 客戶端讀取資產時得到的版本號，服務端使用 CAS 防止覆蓋更新。 */
