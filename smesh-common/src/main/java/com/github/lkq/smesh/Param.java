package com.github.lkq.smesh;

public class Param {

    public static void isPositive(long repeatCount, String expectedMessage) {
        if (repeatCount <= 0) {
            throw new IllegalArgumentException(expectedMessage);
        }
    }

    public static void isNotNull(Object value, String expectedMessage) {
        if (value == null) {
            throw new IllegalArgumentException(expectedMessage);
        }
    }
}
