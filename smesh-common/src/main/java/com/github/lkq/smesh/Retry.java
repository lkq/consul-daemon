package com.github.lkq.smesh;

import java.util.function.Supplier;

public class Retry {
    public static boolean exec(int repeatCount, int interval, Supplier<Boolean> supplier) {
        Param.isPositive(repeatCount, "repeat count should be greater than 0");
        Param.isPositive(interval, "interval should be greater than 0");
        Param.isNotNull(supplier, "supplier should be provided");

        for (int i = 0; i < repeatCount; i++) {
            try {
                Boolean value = supplier.get();
                if (value) {
                    return true;
                }
            } catch (Throwable ignored) {
            } finally {
                delay(interval);
            }
        }
        return false;
    }

    private static void delay(int interval) {
        try {
            Thread.sleep(interval);
        } catch (InterruptedException ignored) { }
    }
}
