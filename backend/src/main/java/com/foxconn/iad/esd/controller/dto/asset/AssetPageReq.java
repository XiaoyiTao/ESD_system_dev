package com.foxconn.iad.esd.controller.dto.asset;

import com.foxconn.iad.esd.controller.dto.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
public class AssetPageReq extends PageQuery {

    /** 只允许查询当前账号有权访问的厂区。 */
    @NotBlank
    private String siteCode;
    private String keyword;
    private Integer assetType;
    private Integer lifecycleStatus;
    private String colorCode;
    private String sizeCode;
}
    /** 匹配资产编码、持有人工号或姓名。 */
    /** 可选资产类型过滤。 */
    /** 可选生命周期状态过滤。 */
    /** 可选颜色精确过滤。 */
    /** 可选尺码精确过滤。 */
