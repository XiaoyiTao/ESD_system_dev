package com.foxconn.iad.module.esd.controller.admin.asset.vo;

import com.foxconn.iad.module.esd.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
public class AssetPageReqVO extends PageQuery {

    /** 只允許查詢當前帳號有權訪問的廠區。 */
    @NotBlank
    private String siteCode;
    private String keyword;
    private Integer assetType;
    private Integer lifecycleStatus;
    private String colorCode;
    private String sizeCode;
}
    /** 匹配資產編碼、持有人工號或姓名。 */
    /** 可選資產類型過濾。 */
    /** 可選生命周期狀態過濾。 */
    /** 可選顏色精確過濾。 */
    /** 可選尺碼精確過濾。 */
