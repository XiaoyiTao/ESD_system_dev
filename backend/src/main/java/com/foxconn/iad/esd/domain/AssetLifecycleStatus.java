package com.foxconn.iad.esd.domain;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public enum AssetLifecycleStatus {
    AVAILABLE(10, "可用庫存"),
    ISSUED(20, "已發放"),
    IN_LAUNDRY(30, "清洗中"),
    RETIRED(40, "已報廢"),
    LOST(50, "已遺失");

    private final int code;
    private final String displayName;
    private static final Map<AssetLifecycleStatus, Set<AssetLifecycleStatus>> TRANSITIONS;

    static {
        Map<AssetLifecycleStatus, Set<AssetLifecycleStatus>> transitions =
                new EnumMap<>(AssetLifecycleStatus.class);
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

    public boolean canTransitionTo(AssetLifecycleStatus target) {
        return target != null && TRANSITIONS.get(this).contains(target);
    }
}

