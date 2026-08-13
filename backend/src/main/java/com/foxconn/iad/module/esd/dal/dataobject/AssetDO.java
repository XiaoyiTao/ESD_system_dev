package com.foxconn.iad.module.esd.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("esd_asset")
public class AssetDO {

    /** 資產主鍵。 */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /** 全局資產編碼，入庫時必須唯一。 */
    private String assetCode;
    /** 資產所屬廠區。 */
    private String siteCode;
    /** 1 靜電衣，2 靜電鞋。 */
    private Integer assetType;
    /** 顏色編碼或顏色名稱。 */
    private String colorCode;
    /** 衣服尺碼或鞋子尺碼。 */
    private String sizeCode;
    /** 生命周期狀態，取 AssetLifecycleStatus 的穩定編碼。 */
    private Integer lifecycleStatus;
    /** 當前持有人平台用戶 ID；僅已發放狀態允許非空。 */
    private Long currentHolderUserId;
    /** 當前持有人工號快照。 */
    private String currentHolderNo;
    /** 當前持有人姓名快照。 */
    private String currentHolderName;
    /** 已完成清洗次數。 */
    private Integer cleanCount;
    /** 業務版本號，用於防止編輯覆蓋並發交易。 */
    private Integer version;
    /** 創建操作人 ID。 */
    private String creator;
    /** 創建時間。 */
    private LocalDateTime createTime;
    /** 最後修改操作人 ID。 */
    private String updater;
    /** 最後修改時間。 */
    private LocalDateTime updateTime;
    /** 邏輯刪除標記。 */
    @TableLogic
    private Boolean deleted;
}
