package com.foxconn.iad.module.esd.controller.admin.issue.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class IssueRespVO {

    /** 發放記錄 ID。 */
    private Long id;
    /** 物品編碼快照。 */
    private String assetCode;
    /** 資產類型：1 靜電衣，2 靜電鞋。 */
    private Integer assetType;
    /** 領用員工平台用戶 ID。 */
    private Long employeeUserId;
    /** 領用員工工號快照。 */
    private String employeeNo;
    /** 領用員工姓名快照。 */
    private String employeeName;
    /** 責任主管姓名快照。 */
    private String supervisorName;
    /** 課別名稱快照。 */
    private String deptName;
    /** 樓層代碼快照。 */
    private String floorCode;
    /** 發放操作人姓名快照。 */
    private String issueOperatorName;
    /** 發放日期。 */
    private LocalDate issueDate;
    /** 記錄狀態，取 BusinessRecordStatus 的編碼。 */
    private Integer recordStatus;
    /** 歸還結算時間，未歸還為空。 */
    private LocalDateTime closeTime;
}
