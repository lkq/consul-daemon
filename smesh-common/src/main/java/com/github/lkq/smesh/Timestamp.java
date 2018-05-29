package com.github.lkq.smesh;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Timestamp {
    public static final int SECONDS_OF_DAY = 86400;
    public static final int SECONDS_OF_HOUR = 3600;
    public static final int SECONDS_OF_MINUTE = 60;

    private static long startTime = System.currentTimeMillis();

    public static String get() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public static String upTime() {

        long totalSeconds = (System.currentTimeMillis() - startTime) / 1000;
        long days = totalSeconds / SECONDS_OF_DAY;
        long remainsHours = totalSeconds % SECONDS_OF_DAY;
        long hours = remainsHours / SECONDS_OF_HOUR;
        long minutes = remainsHours % SECONDS_OF_HOUR / SECONDS_OF_MINUTE;

        StringBuilder upTime = new StringBuilder();
        if (days > 0) {
            upTime.append(days).append("d ");
        }
        upTime.append(String.format("%02d", hours))
                .append(":")
                .append(String.format("%02d", minutes));
        return upTime.toString();
    }
}
