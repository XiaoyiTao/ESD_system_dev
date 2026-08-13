package com.foxconn.iad.module.esd.controller.admin.asset.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AssetRespVO {

    /** 資產主鍵。 */
    private Long id;
    /** 資產編碼。 */
    private String assetCode;
    /** 所屬廠區。 */
    private String siteCode;
    /** 1 靜電衣，2 靜電鞋。 */
    private Integer assetType;
    /** 顏色。 */
    private String colorCode;
    /** 尺碼。 */
    private String sizeCode;
    /** 生命周期狀態編碼。 */
    private Integer lifecycleStatus;
    /** 生命周期狀態中文名稱。 */
    private String lifecycleStatusName;
    /** 當前持有人平台用戶 ID。 */
    private Long currentHolderUserId;
    /** 當前持有人工號。 */
    private String currentHolderNo;
    /** 當前持有人姓名。 */
    private String currentHolderName;
    /** 完成清洗的累計次數。 */
    private Integer cleanCount;
    /** 編輯樂觀鎖版本號。 */
    private Integer version;
    /** 入庫時間。 */
    private LocalDateTime createTime;
    /** 最後修改時間。 */
    private LocalDateTime updateTime;
    /** 最近一次發放記錄，無發放歷史為空。 */
    private LatestIssueVO latestIssue;
    /** 最近一次清洗記錄，無清洗歷史為空。 */
    private LatestLaundryVO latestLaundry;

    /** 資產詳情中的最近一次發放快照。 */
    @Data
    public static class LatestIssueVO {
        /** 發放日期。 */
        private LocalDate issueDate;
        /** 領用員工工號。 */
        private String employeeNo;
        /** 領用員工姓名。 */
        private String employeeName;
        /** 發放操作人姓名。 */
        private String issueOperatorName;
        /** 歸還結算日期，未歸還為空。 */
        private LocalDate returnDate;
    }

    /** 資產詳情中的最近一次清洗快照。 */
    @Data
    public static class LatestLaundryVO {
        /** 送洗時間。 */
        private LocalDateTime sendTime;
        /** 完成清洗時間，未完成為空。 */
        private LocalDateTime completeTime;
    }
}

