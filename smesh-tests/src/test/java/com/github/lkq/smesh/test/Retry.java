package com.github.lkq.smesh.test;

import com.github.lkq.paramer.Paramer;

import java.util.function.Supplier;

public class Retry {
    public static boolean exec(int repeatCount, int interval, Supplier<Boolean> supplier) {
        Paramer.requires().positive(repeatCount, "repeat count should be greater than 0")
                .positive(interval, "interval should be greater than 0")
                .notNull(supplier, "supplier should be provided");

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
        } catch (InterruptedException ignored) {
        }
    }
}
