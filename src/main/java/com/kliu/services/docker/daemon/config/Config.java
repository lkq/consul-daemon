package com.kliu.services.docker.daemon.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
