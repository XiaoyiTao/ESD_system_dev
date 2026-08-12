package com.foxconn.iad.esd.domain;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * 资产生命周期状态及允许的业务流转。
 *
 * <p>数值与 MySQL 表约束及前端展示保持一致：10 可用库存、20 已发放、
 * 30 清洗中、40 已报废、50 已遗失。</p>
 */
public enum AssetLifecycleStatus {
    /** 可发放的干净库存。 */
    AVAILABLE(10, "可用庫存"),
    /** 已由员工领用。 */
    ISSUED(20, "已發放"),
    /** 回收后已送洗、尚未完成清洗。 */
    IN_LAUNDRY(30, "清洗中"),
    /** 已终止使用，正常流程不可再次发放。 */
    RETIRED(40, "已報廢"),
    /** 资产已确认丢失，正常流程不可再次发放。 */
    LOST(50, "已遺失");

    /** 对外及数据库使用的稳定状态编码。 */
    private final int code;
    /** 面向用户的中文名称。 */
    private final String displayName;
    /** 每个源状态允许进入的目标状态集合。 */
    private static final Map<AssetLifecycleStatus, Set<AssetLifecycleStatus>> TRANSITIONS;

    static {
        Map<AssetLifecycleStatus, Set<AssetLifecycleStatus>> transitions =
                new EnumMap<>(AssetLifecycleStatus.class);
        // 发放、回收、送洗和终止操作必须经过这些白名单状态转换。
        transitions.put(AVAILABLE, EnumSet.of(ISSUED, RETIRED, LOST));
        transitions.put(ISSUED, EnumSet.of(AVAILABLE, IN_LAUNDRY, RETIRED, LOST));
        transitions.put(IN_LAUNDRY, EnumSet.of(AVAILABLE, RETIRED, LOST));
        transitions.put(RETIRED, EnumSet.of(AVAILABLE, ISSUED, IN_LAUNDRY));
        transitions.put(LOST, EnumSet.of(AVAILABLE, ISSUED, IN_LAUNDRY));
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

    /** 判断当前状态是否允许直接流转到目标状态。 */
    public boolean canTransitionTo(AssetLifecycleStatus target) {
        return target != null && TRANSITIONS.get(this).contains(target);
    }
}
