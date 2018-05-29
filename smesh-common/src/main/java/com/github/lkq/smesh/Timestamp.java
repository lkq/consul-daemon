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

        long upTime = (System.currentTimeMillis() - startTime) / 1000;
        long days = upTime / SECONDS_OF_DAY;
        long remainsHours = upTime % SECONDS_OF_DAY;
        long hours = remainsHours / SECONDS_OF_HOUR;
        long minutes = remainsHours % SECONDS_OF_HOUR / SECONDS_OF_MINUTE;

        return String.valueOf(days) + "d " +
                hours + ":" +
                minutes;
    }
}
