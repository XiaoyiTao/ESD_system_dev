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

