package com.kliu.services.docker.daemon.config;

public class Config {

    private static ConfigProvider provider;

    public static void init(ConfigProvider provider) {
        if (Config.provider != null) {
            throw new IllegalStateException("do not re-init config");
        }
        Config.provider = provider;
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
}
