package com.foxconn.iad.module.esd.enums;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * 資產生命周期狀態及允許的業務流轉。
 *
 * <p>數值與 MySQL 表約束及前端展示保持一致：10 庫存、20 發放中、25 待送洗、
 * 30 清洗中、40 報廢、50 丟失。</p>
 */
public enum AssetLifecycleStatus {
    /** 可發放的乾淨庫存。 */
    AVAILABLE(10, "庫存"),
    /** 已由員工領用。 */
    ISSUED(20, "發放中"),
    /** 回收後已指定送洗、尚未開始清洗。 */
    PENDING_LAUNDRY(25, "待送洗"),
    /** 已送洗、正在清洗，尚未完成。 */
    IN_LAUNDRY(30, "清洗中"),
    /** 已終止使用，正常流程不可再次發放。 */
    RETIRED(40, "報廢"),
    /** 資產已確認丟失，正常流程不可再次發放。 */
    LOST(50, "丟失");

    /** 對外及資料庫使用的穩定狀態編碼。 */
    private final int code;
    /** 面向用戶的中文名稱。 */
    private final String displayName;
    /** 每個源狀態允許進入的目標狀態集合。 */
    private static final Map<AssetLifecycleStatus, Set<AssetLifecycleStatus>> TRANSITIONS;

    static {
        Map<AssetLifecycleStatus, Set<AssetLifecycleStatus>> transitions =
                new EnumMap<>(AssetLifecycleStatus.class);
        // 發放、回收、送洗和終止操作必須經過這些白名單狀態轉換。
        transitions.put(AVAILABLE, EnumSet.of(ISSUED, RETIRED, LOST));
        transitions.put(ISSUED, EnumSet.of(AVAILABLE, PENDING_LAUNDRY, RETIRED, LOST));
        transitions.put(PENDING_LAUNDRY, EnumSet.of(IN_LAUNDRY, RETIRED, LOST));
        transitions.put(IN_LAUNDRY, EnumSet.of(AVAILABLE, RETIRED, LOST));
        transitions.put(RETIRED, EnumSet.of(AVAILABLE, ISSUED, PENDING_LAUNDRY, IN_LAUNDRY));
        transitions.put(LOST, EnumSet.of(AVAILABLE, ISSUED, PENDING_LAUNDRY, IN_LAUNDRY));
        TRANSITIONS = Collections.unmodifiableMap(transitions);
    }

    AssetLifecycleStatus(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public int getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    /** 判斷當前狀態是否允許直接流轉到目標狀態。 */
    public boolean canTransitionTo(AssetLifecycleStatus target) {
        return target != null && TRANSITIONS.get(this).contains(target);
    }

    /** 依資料庫編碼查詢中文名稱，未知或空編碼返回 null。 */
    public static String displayNameOf(Integer code) {
        if (code == null) {
            return null;
        }
        for (AssetLifecycleStatus status : values()) {
            if (status.getCode() == code) {
                return status.getDisplayName();
            }
        }
        return null;
    }
}
