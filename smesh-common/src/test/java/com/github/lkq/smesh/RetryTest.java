package com.github.lkq.smesh;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RetryTest {

    @Test
    void willReturnTrueWithOneRetry() {
        assertTrue(Retry.exec(3, 1, () -> true));
    }

    @Test
    void willReturnTrueWithTwoRetry() {
        Boolean[] values = new Boolean[]{false, true};
        final int[] index = {0};
        assertTrue(Retry.exec(3, 1, () -> values[index[0]++]));
    }

    @Test
    void willReturnTrueWithRetryException() {
        final int[] index = {0};
        assertTrue(Retry.exec(3, 1, () -> {
            if (index[0]++ == 0) {
                throw new RuntimeException("mock error");
            }
            return true;
        }));

    }

    @Test
    void willReturnFalseIfAllRetryFailed() {
        assertFalse(Retry.exec(3, 1, () -> false));
    }

    @Test
    void willReturnFalseIfAllRetryHaveException() {
        assertFalse(Retry.exec(3, 1, () -> {
                throw new RuntimeException("mock error");
        }));
    }
}