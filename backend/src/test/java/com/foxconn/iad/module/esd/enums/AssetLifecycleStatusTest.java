package com.foxconn.iad.module.esd.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssetLifecycleStatusTest {

    @Test
    @DisplayName("可用庫存可直接發放或終止")
    void availableCanBeIssuedOrTerminated() {
        assertTrue(AssetLifecycleStatus.AVAILABLE.canTransitionTo(AssetLifecycleStatus.ISSUED));
        assertTrue(AssetLifecycleStatus.AVAILABLE.canTransitionTo(AssetLifecycleStatus.RETIRED));
        assertTrue(AssetLifecycleStatus.AVAILABLE.canTransitionTo(AssetLifecycleStatus.LOST));
        assertFalse(AssetLifecycleStatus.AVAILABLE.canTransitionTo(AssetLifecycleStatus.PENDING_LAUNDRY));
        assertFalse(AssetLifecycleStatus.AVAILABLE.canTransitionTo(AssetLifecycleStatus.IN_LAUNDRY));
    }

    @Test
    @DisplayName("發放中可回收入庫或送洗")
    void issuedCanBeRestockedOrSentToLaundry() {
        assertTrue(AssetLifecycleStatus.ISSUED.canTransitionTo(AssetLifecycleStatus.AVAILABLE));
        assertTrue(AssetLifecycleStatus.ISSUED.canTransitionTo(AssetLifecycleStatus.PENDING_LAUNDRY));
        // 發放中不能跳過待送洗直接進入清洗中。
        assertFalse(AssetLifecycleStatus.ISSUED.canTransitionTo(AssetLifecycleStatus.IN_LAUNDRY));
    }

    @Test
    @DisplayName("待送洗只能送洗登記或終止")
    void pendingLaundryCanOnlyBeStartedOrTerminated() {
        assertTrue(AssetLifecycleStatus.PENDING_LAUNDRY.canTransitionTo(AssetLifecycleStatus.IN_LAUNDRY));
        assertTrue(AssetLifecycleStatus.PENDING_LAUNDRY.canTransitionTo(AssetLifecycleStatus.RETIRED));
        assertTrue(AssetLifecycleStatus.PENDING_LAUNDRY.canTransitionTo(AssetLifecycleStatus.LOST));
        // 待送洗不能直接回到庫存或發放中。
        assertFalse(AssetLifecycleStatus.PENDING_LAUNDRY.canTransitionTo(AssetLifecycleStatus.AVAILABLE));
        assertFalse(AssetLifecycleStatus.PENDING_LAUNDRY.canTransitionTo(AssetLifecycleStatus.ISSUED));
    }

    @Test
    @DisplayName("清洗中完成後回到庫存但不得再次發放或送洗")
    void inLaundryCanOnlyBeCompleted() {
        assertTrue(AssetLifecycleStatus.IN_LAUNDRY.canTransitionTo(AssetLifecycleStatus.AVAILABLE));
        assertFalse(AssetLifecycleStatus.IN_LAUNDRY.canTransitionTo(AssetLifecycleStatus.ISSUED));
        assertFalse(AssetLifecycleStatus.IN_LAUNDRY.canTransitionTo(AssetLifecycleStatus.PENDING_LAUNDRY));
        assertFalse(AssetLifecycleStatus.IN_LAUNDRY.canTransitionTo(AssetLifecycleStatus.IN_LAUNDRY));
    }

    @Test
    @DisplayName("終止狀態之間不能直接互相流轉")
    void terminalStatusesCannotTransitionToEachOther() {
        assertFalse(AssetLifecycleStatus.RETIRED.canTransitionTo(AssetLifecycleStatus.LOST));
        assertFalse(AssetLifecycleStatus.LOST.canTransitionTo(AssetLifecycleStatus.RETIRED));
    }

    @Test
    @DisplayName("撤銷可將終止狀態恢復到前置狀態")
    void reverseCanRestoreTerminalStatus() {
        assertTrue(AssetLifecycleStatus.RETIRED.canTransitionTo(AssetLifecycleStatus.AVAILABLE));
        assertTrue(AssetLifecycleStatus.RETIRED.canTransitionTo(AssetLifecycleStatus.ISSUED));
        assertTrue(AssetLifecycleStatus.RETIRED.canTransitionTo(AssetLifecycleStatus.PENDING_LAUNDRY));
        assertTrue(AssetLifecycleStatus.RETIRED.canTransitionTo(AssetLifecycleStatus.IN_LAUNDRY));
        assertTrue(AssetLifecycleStatus.LOST.canTransitionTo(AssetLifecycleStatus.AVAILABLE));
    }
}
