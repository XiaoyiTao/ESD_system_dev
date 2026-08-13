package com.foxconn.iad.module.esd.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssetLifecycleStatusTest {

    @Test
    void 可用庫存可直接發放或終止() {
        assertTrue(AssetLifecycleStatus.AVAILABLE.canTransitionTo(AssetLifecycleStatus.ISSUED));
        assertTrue(AssetLifecycleStatus.AVAILABLE.canTransitionTo(AssetLifecycleStatus.RETIRED));
        assertTrue(AssetLifecycleStatus.AVAILABLE.canTransitionTo(AssetLifecycleStatus.LOST));
        assertFalse(AssetLifecycleStatus.AVAILABLE.canTransitionTo(AssetLifecycleStatus.PENDING_LAUNDRY));
        assertFalse(AssetLifecycleStatus.AVAILABLE.canTransitionTo(AssetLifecycleStatus.IN_LAUNDRY));
    }

    @Test
    void 發放中可回收入庫或送洗() {
        assertTrue(AssetLifecycleStatus.ISSUED.canTransitionTo(AssetLifecycleStatus.AVAILABLE));
        assertTrue(AssetLifecycleStatus.ISSUED.canTransitionTo(AssetLifecycleStatus.PENDING_LAUNDRY));
        // 發放中不能跳過待送洗直接進入清洗中。
        assertFalse(AssetLifecycleStatus.ISSUED.canTransitionTo(AssetLifecycleStatus.IN_LAUNDRY));
    }

    @Test
    void 待送洗只能送洗登記或終止() {
        assertTrue(AssetLifecycleStatus.PENDING_LAUNDRY.canTransitionTo(AssetLifecycleStatus.IN_LAUNDRY));
        assertTrue(AssetLifecycleStatus.PENDING_LAUNDRY.canTransitionTo(AssetLifecycleStatus.RETIRED));
        assertTrue(AssetLifecycleStatus.PENDING_LAUNDRY.canTransitionTo(AssetLifecycleStatus.LOST));
        // 待送洗不能直接回到庫存或發放中。
        assertFalse(AssetLifecycleStatus.PENDING_LAUNDRY.canTransitionTo(AssetLifecycleStatus.AVAILABLE));
        assertFalse(AssetLifecycleStatus.PENDING_LAUNDRY.canTransitionTo(AssetLifecycleStatus.ISSUED));
    }

    @Test
    void 清洗中完成後回到庫存但不得再次發放或送洗() {
        assertTrue(AssetLifecycleStatus.IN_LAUNDRY.canTransitionTo(AssetLifecycleStatus.AVAILABLE));
        assertFalse(AssetLifecycleStatus.IN_LAUNDRY.canTransitionTo(AssetLifecycleStatus.ISSUED));
        assertFalse(AssetLifecycleStatus.IN_LAUNDRY.canTransitionTo(AssetLifecycleStatus.PENDING_LAUNDRY));
        assertFalse(AssetLifecycleStatus.IN_LAUNDRY.canTransitionTo(AssetLifecycleStatus.IN_LAUNDRY));
    }

    @Test
    void 終止狀態之間不能直接互相流轉() {
        assertFalse(AssetLifecycleStatus.RETIRED.canTransitionTo(AssetLifecycleStatus.LOST));
        assertFalse(AssetLifecycleStatus.LOST.canTransitionTo(AssetLifecycleStatus.RETIRED));
    }

    @Test
    void 撤銷可將終止狀態恢復到前置狀態() {
        assertTrue(AssetLifecycleStatus.RETIRED.canTransitionTo(AssetLifecycleStatus.AVAILABLE));
        assertTrue(AssetLifecycleStatus.RETIRED.canTransitionTo(AssetLifecycleStatus.ISSUED));
        assertTrue(AssetLifecycleStatus.RETIRED.canTransitionTo(AssetLifecycleStatus.PENDING_LAUNDRY));
        assertTrue(AssetLifecycleStatus.RETIRED.canTransitionTo(AssetLifecycleStatus.IN_LAUNDRY));
        assertTrue(AssetLifecycleStatus.LOST.canTransitionTo(AssetLifecycleStatus.AVAILABLE));
    }
}
