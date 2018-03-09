package com.lkq.services.docker.daemon.config;

import com.lkq.services.docker.daemon.aws.AWSClient;
import spark.utils.StringUtils;

public class Environment {

    public static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        if (StringUtils.isEmpty(value)) {
            value = System.getProperty(key, defaultValue);
        }
        if (StringUtils.isEmpty(value)) {
            value = AWSClient.instance().getTag(key, defaultValue);
        }
        return value;
    }
}
