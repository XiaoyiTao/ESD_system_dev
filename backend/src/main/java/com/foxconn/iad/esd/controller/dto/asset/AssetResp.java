package com.foxconn.iad.esd.controller.dto.asset;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssetResp {

    /** 资产主键。 */
    private Long id;
    /** 资产编码。 */
    private String assetCode;
    /** 所属厂区。 */
    private String siteCode;
    /** 1 静电衣，2 静电鞋。 */
    private Integer assetType;
    /** 颜色。 */
    private String colorCode;
    /** 尺码。 */
    private String sizeCode;
    /** 生命周期状态编码。 */
    private Integer lifecycleStatus;
    /** 生命周期状态中文名称。 */
    private String lifecycleStatusName;
    /** 当前持有人平台用户 ID。 */
    private Long currentHolderUserId;
    /** 当前持有人工号。 */
    private String currentHolderNo;
    /** 当前持有人姓名。 */
    private String currentHolderName;
    /** 完成清洗的累计次数。 */
    private Integer cleanCount;
    /** 编辑乐观锁版本号。 */
    private Integer version;
    /** 入库时间。 */
    private LocalDateTime createTime;
    /** 最后修改时间。 */
    private LocalDateTime updateTime;
}
