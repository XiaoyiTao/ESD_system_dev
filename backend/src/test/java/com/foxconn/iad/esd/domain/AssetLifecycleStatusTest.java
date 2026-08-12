package com.foxconn.iad.esd.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssetLifecycleStatusTest {

    @Test
    /** 可用库存可以直接发放，但不能跳过发放进入清洗。 */
    void issuedCanBeRestockedOrSentToLaundry() {
        assertTrue(AssetLifecycleStatus.ISSUED.canTransitionTo(AssetLifecycleStatus.AVAILABLE));
        assertTrue(AssetLifecycleStatus.ISSUED.canTransitionTo(AssetLifecycleStatus.IN_LAUNDRY));
    }

    @Test
    /** 清洗中的资产不能再次进入发放或回收流程。 */
    void laundryCannotEnterReturnFlow() {
        assertFalse(AssetLifecycleStatus.IN_LAUNDRY.canTransitionTo(AssetLifecycleStatus.ISSUED));
        assertFalse(AssetLifecycleStatus.IN_LAUNDRY.canTransitionTo(AssetLifecycleStatus.IN_LAUNDRY));
    }

    @Test
    /** 报废和遗失只能由后续明确的撤销交易恢复。 */
    void terminalStatusCanOnlyBeRestoredByExplicitReverseOperation() {
        assertFalse(AssetLifecycleStatus.RETIRED.canTransitionTo(AssetLifecycleStatus.LOST));
        assertFalse(AssetLifecycleStatus.LOST.canTransitionTo(AssetLifecycleStatus.RETIRED));
    }
}
