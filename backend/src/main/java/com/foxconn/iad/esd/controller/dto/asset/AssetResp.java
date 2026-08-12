package com.foxconn.iad.esd.controller.dto.asset;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssetResp {

    private Long id;
    private String assetCode;
    private String siteCode;
    private Integer assetType;
    private String colorCode;
    private String sizeCode;
    private Integer lifecycleStatus;
    private String lifecycleStatusName;
    private Long currentHolderUserId;
    private String currentHolderNo;
    private String currentHolderName;
    private Integer cleanCount;
    private Integer version;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

