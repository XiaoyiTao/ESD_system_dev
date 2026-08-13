package com.foxconn.iad.module.esd.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("esd_issue_record")
public class IssueRecordDO {

    /** 發放記錄主鍵。 */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /** 冪等鍵，防止重複提交。 */
    private String requestId;
    /** 廠區編碼。 */
    private String siteCode;
    /** 資產 ID。 */
    private Long assetId;
    /** 資產編碼快照。 */
    private String assetCode;
    /** 資產類型快照：1 靜電衣，2 靜電鞋。 */
    private Integer assetType;
    /** 領用員工平台用戶 ID。 */
    private Long employeeUserId;
    /** 領用員工工號快照。 */
    private String employeeNo;
    /** 領用員工姓名快照。 */
    private String employeeName;
    /** 責任主管姓名快照，發放時從人員檔寫入。 */
    private String supervisorName;
    /** 課別名稱快照，發放時從人員檔寫入。 */
    private String deptName;
    /** 樓層代碼快照，發放時從人員檔寫入。 */
    private String floorCode;
    /** 發放操作人平台用戶 ID。 */
    private Long issueOperatorUserId;
    /** 發放操作人姓名快照。 */
    private String issueOperatorName;
    /** 發放日期。 */
    private LocalDate issueDate;
    /** 記錄狀態，取 BusinessRecordStatus 的編碼。 */
    private Integer recordStatus;
    /** 歸還結算時間。 */
    private LocalDateTime closeTime;
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
