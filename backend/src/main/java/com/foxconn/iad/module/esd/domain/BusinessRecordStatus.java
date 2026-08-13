package com.foxconn.iad.module.esd.domain;

/**
 * 業務交易記錄的通用狀態。
 *
 * <p>與資料庫 record_status 欄位的數值保持一致，交易記錄保留完整審計、
 * 不物理刪除，僅在業務結束時流轉到對應終態。</p>
 */
public enum BusinessRecordStatus {
    /** 進行中，尚未結算。 */
    ACTIVE(1),
    /** 已完成結算。 */
    COMPLETED(2),
    /** 被後續交易終止。 */
    TERMINATED(3),
    /** 被撤銷。 */
    REVERSED(4);

    /** 資料庫使用的穩定編碼。 */
    private final int code;

    BusinessRecordStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
