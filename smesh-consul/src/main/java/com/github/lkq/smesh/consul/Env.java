package com.github.lkq.smesh.consul;

import spark.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Env {

    public static List<String> getList(String key, String separator) {
        String value = get(key);

        List<String> results = new ArrayList<>();
        if (StringUtils.isNotEmpty(value)) {
            String[] split = value.split(separator);
            for (String elm : split) {
                if (StringUtils.isNotEmpty(elm)) {
                    results.add(elm);
                }
            }
        }
        return results;
    }

    public static String get(String key) {
        String value = System.getenv(key);
        if (StringUtils.isEmpty(value)) {
            value = System.getProperty(key);
        }
        return value;
    }
}
