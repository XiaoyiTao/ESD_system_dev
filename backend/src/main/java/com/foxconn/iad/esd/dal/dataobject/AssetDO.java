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

    /** 资产主键。 */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /** 全局资产编码，入库时必须唯一。 */
    private String assetCode;
    /** 资产所属厂区。 */
    private String siteCode;
    /** 1 静电衣，2 静电鞋。 */
    private Integer assetType;
    /** 颜色编码或颜色名称。 */
    private String colorCode;
    /** 衣服尺码或鞋子尺码。 */
    private String sizeCode;
    /** 生命周期状态，取 AssetLifecycleStatus 的稳定编码。 */
    private Integer lifecycleStatus;
    /** 当前持有人平台用户 ID；仅已发放状态允许非空。 */
    private Long currentHolderUserId;
    /** 当前持有人工号快照。 */
    private String currentHolderNo;
    /** 当前持有人姓名快照。 */
    private String currentHolderName;
    /** 已完成清洗次数。 */
    private Integer cleanCount;
    /** 业务版本号，用于防止编辑覆盖并发交易。 */
    private Integer version;
    /** 创建操作人 ID。 */
    private String creator;
    /** 创建时间。 */
    private LocalDateTime createTime;
    /** 最后修改操作人 ID。 */
    private String updater;
    /** 最后修改时间。 */
    private LocalDateTime updateTime;
    /** 逻辑删除标记。 */
    @TableLogic
    private Boolean deleted;
}
