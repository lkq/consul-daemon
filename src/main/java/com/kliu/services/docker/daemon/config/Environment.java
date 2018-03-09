package com.kliu.services.docker.daemon.config;

import com.kliu.services.docker.daemon.aws.AWSClientFactory;
import spark.utils.StringUtils;

public class Environment {

    public static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        if (StringUtils.isEmpty(value)) {
            value = System.getProperty(key, defaultValue);
        }
        if (StringUtils.isEmpty(value)) {
            value = AWSClientFactory.get().getTag(key, defaultValue);
        }
        return value;
    }
}
