package com.kliu.services.docker.daemon.config.env;

import spark.utils.StringUtils;

public class Environment {

    public static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        if (StringUtils.isEmpty(value)) {
            value = System.getProperty(key, defaultValue);
        }
        return value;
    }
}
