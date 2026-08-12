package com.foxconn.iad.esd.controller.dto.asset;

import com.foxconn.iad.esd.controller.dto.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
public class AssetPageReq extends PageQuery {

    @NotBlank
    private String siteCode;
    private String keyword;
    private Integer assetType;
    private Integer lifecycleStatus;
    private String colorCode;
    private String sizeCode;
}

