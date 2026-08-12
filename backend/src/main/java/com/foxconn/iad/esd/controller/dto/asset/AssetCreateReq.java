package com.foxconn.iad.esd.controller.dto.asset;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class AssetCreateReq {

    /** 资产编码，只允许便于标签打印和扫描的 ASCII 字符。 */
    @NotBlank
    @Size(max = 64)
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "资产编码只能包含字母、数字、下划线和连字符")
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
    /** 入库所属厂区；服务端会与当前登录用户的厂区权限比对。 */
    /** 1 静电衣，2 静电鞋。 */
    /** 颜色编码或名称。 */
    /** 衣服尺码或鞋子尺码。 */
