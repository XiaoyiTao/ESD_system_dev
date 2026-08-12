package com.foxconn.iad.esd.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("esd_asset")
public class AssetDO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String assetCode;
    private String siteCode;
    private Integer assetType;
    private String colorCode;
    private String sizeCode;
    private Integer lifecycleStatus;
    private Long currentHolderUserId;
    private String currentHolderNo;
    private String currentHolderName;
    private Integer cleanCount;
    private Integer version;
    private String creator;
    private LocalDateTime createTime;
    private String updater;
    private LocalDateTime updateTime;
    @TableLogic
    private Boolean deleted;
}

