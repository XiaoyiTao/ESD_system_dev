package com.foxconn.iad.module.esd.enums;

/**
 * 回收後續處置方式。
 *
 * <p>數值與資料庫 disposition 欄位一致。</p>
 */
public enum ReturnDisposition {
    /** 直接入庫。 */
    RESTOCK(1, "直接入庫"),
    /** 送洗。 */
    LAUNDER(2, "送洗");

    /** 資料庫使用的穩定編碼。 */
    private final int code;
    /** 面向用戶的中文名稱。 */
    private final String displayName;

    ReturnDisposition(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public int getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    /** 依資料庫編碼查詢中文名稱，未知或空編碼返回 null。 */
    public static String displayNameOf(Integer code) {
        if (code == null) {
            return null;
        }
        for (ReturnDisposition disposition : values()) {
            if (disposition.getCode() == code) {
                return disposition.getDisplayName();
            }
        }
        return null;
    }
}
