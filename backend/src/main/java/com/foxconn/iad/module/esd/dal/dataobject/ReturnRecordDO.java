package com.foxconn.iad.module.esd.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("esd_return_record")
public class ReturnRecordDO {

    /** 回收記錄主鍵。 */
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
    /** 資產類型快照：1 靜電衣，2 靜電鞋。 */
    private Integer assetType;
    /** 被結算的發放記錄 ID。 */
    private Long issueRecordId;
    /** 歸還員工平台用戶 ID。 */
    private Long employeeUserId;
    /** 歸還員工工號快照。 */
    private String employeeNo;
    /** 歸還員工姓名快照。 */
    private String employeeName;
    /** 後續處置：1 直接入庫，2 送洗。 */
    private Integer disposition;
    /** 歸還人平台用戶 ID，可空。 */
    private Long returnerUserId;
    /** 歸還人姓名快照。 */
    private String returnerName;
    /** 回收操作人平台用戶 ID。 */
    private Long receiverUserId;
    /** 回收操作人姓名快照。 */
    private String receiverName;
    /** 回收日期。 */
    private LocalDate returnDate;
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
