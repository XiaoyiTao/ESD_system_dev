package com.foxconn.iad.module.esd.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("esd_laundry_record")
public class LaundryRecordDO {

    /** 清洗記錄主鍵。 */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /** 冪等鍵。 */
    private String requestId;
    /** 廠區編碼。 */
    private String siteCode;
    /** 資產 ID。 */
    private Long assetId;
    /** 資產編碼快照。 */
    private String assetCode;
    /** 產生本次清洗的回收記錄 ID。 */
    private Long returnRecordId;
    /** 送洗操作人平台用戶 ID。 */
    private Long sendOperatorUserId;
    /** 送洗操作人姓名快照。 */
    private String sendOperatorName;
    /** 送洗時間，回收選送洗時即寫入。 */
    private LocalDateTime sendTime;
    /** 完成清洗操作人平台用戶 ID。 */
    private Long completeOperatorUserId;
    /** 完成清洗操作人姓名快照。 */
    private String completeOperatorName;
    /** 完成清洗時間。 */
    private LocalDateTime completeTime;
    /** 記錄狀態，取 BusinessRecordStatus 的編碼。 */
    private Integer recordStatus;
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
