package com.foxconn.iad.esd.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssetLifecycleStatusTest {

    @Test
    void issuedCanBeRestockedOrSentToLaundry() {
        assertTrue(AssetLifecycleStatus.ISSUED.canTransitionTo(AssetLifecycleStatus.AVAILABLE));
        assertTrue(AssetLifecycleStatus.ISSUED.canTransitionTo(AssetLifecycleStatus.IN_LAUNDRY));
    }

    @Test
    void laundryCannotEnterReturnFlow() {
        assertFalse(AssetLifecycleStatus.IN_LAUNDRY.canTransitionTo(AssetLifecycleStatus.ISSUED));
        assertFalse(AssetLifecycleStatus.IN_LAUNDRY.canTransitionTo(AssetLifecycleStatus.IN_LAUNDRY));
    }

    @Test
    void terminalStatusCanOnlyBeRestoredByExplicitReverseOperation() {
        assertFalse(AssetLifecycleStatus.RETIRED.canTransitionTo(AssetLifecycleStatus.LOST));
        assertFalse(AssetLifecycleStatus.LOST.canTransitionTo(AssetLifecycleStatus.RETIRED));
    }
}

