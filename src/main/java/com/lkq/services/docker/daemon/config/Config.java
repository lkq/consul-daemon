package com.lkq.services.docker.daemon.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import java.util.List;

public class Config {

    private static Logger logger = LoggerFactory.getLogger(Config.class);

    private static ConfigProvider provider;

    public static void init(ConfigProvider provider) {
        if (Config.provider != null) {
            logger.warn("do not re-init config");
        } else {
            Config.provider = provider;
        }
    }

    public static String getRegistryURL() {
        return provider.getRegistryURL();
    }

    public static String getConfigPath() {
        return provider.getConfigPath();
    }

    public static String getDataPath() {
        return provider.getDataPath();
    }

    public static String getNetwork() {
        return provider.getNetwork();
    }

    public static String getBootstrapCount() {
        return provider.getBootstrapCount();
    }

    public static List<String> getRetryJoin() {
        return provider.getRetryJoin();
    }

    public static int[] getTCPPorts() {
        return provider.getTCPPort();
    }

    public static int[] getUDPPorts() {
        return provider.getUDPPorts();
    }

    public static String getEnv(String key) {
        return getEnv(key, null);
    }

    public static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        if (StringUtils.isEmpty(value)) {
            value = System.getProperty(key, defaultValue);
        }
        return value;
    }
}
