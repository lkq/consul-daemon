package com.github.lkq.smesh.consul.utils;

public class ConsulUtils {
    public static int parseInt(String value, int defaultValue) {
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }
}
