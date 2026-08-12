package com.foxconn.iad.esd.controller.dto.asset;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AssetUpdateReq {

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
