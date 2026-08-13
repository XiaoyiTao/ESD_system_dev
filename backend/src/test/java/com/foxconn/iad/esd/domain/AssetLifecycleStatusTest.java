package com.foxconn.iad.esd.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssetLifecycleStatusTest {

    @Test
    /** 可用庫存可以直接發放，但不能跳過發放進入清洗。 */
    void issuedCanBeRestockedOrSentToLaundry() {
        assertTrue(AssetLifecycleStatus.ISSUED.canTransitionTo(AssetLifecycleStatus.AVAILABLE));
        assertTrue(AssetLifecycleStatus.ISSUED.canTransitionTo(AssetLifecycleStatus.IN_LAUNDRY));
    }

    @Test
    /** 清洗中的資產不能再次進入發放或回收流程。 */
    void laundryCannotEnterReturnFlow() {
        assertFalse(AssetLifecycleStatus.IN_LAUNDRY.canTransitionTo(AssetLifecycleStatus.ISSUED));
        assertFalse(AssetLifecycleStatus.IN_LAUNDRY.canTransitionTo(AssetLifecycleStatus.IN_LAUNDRY));
    }

    @Test
    /** 報廢和遺失只能由後續明確的撤銷交易恢復。 */
    void terminalStatusCanOnlyBeRestoredByExplicitReverseOperation() {
        assertFalse(AssetLifecycleStatus.RETIRED.canTransitionTo(AssetLifecycleStatus.LOST));
        assertFalse(AssetLifecycleStatus.LOST.canTransitionTo(AssetLifecycleStatus.RETIRED));
    }
}
