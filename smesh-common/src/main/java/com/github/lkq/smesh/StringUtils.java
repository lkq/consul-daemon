package com.github.lkq.smesh;

public class StringUtils {
    public static boolean isNotEmpty(String value) {
        return value != null && !"".equals(value.trim());
    }
}
