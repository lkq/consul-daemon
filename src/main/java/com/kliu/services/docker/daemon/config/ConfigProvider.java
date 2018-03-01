package com.kliu.services.docker.daemon.config;

public class ConfigProvider {

    public static final String NET_EASY_HUB = "http://hub-mirror.c.163.com";

    public String getRegistryURL() {
        return NET_EASY_HUB;
    }

    public String getConfigPath() {
        // TODO get current dir
        return ConfigProvider.class.getClassLoader().getResource(".").getPath() + "/config";
    }

    public String getDataPath() {
        // TODO get current dir
        return ConfigProvider.class.getClassLoader().getResource(".").getPath() + "/data";
    }
}
