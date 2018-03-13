package com.lkq.services.docker.daemon;

import com.lkq.services.docker.daemon.config.ConfigProvider;

public class TestConfigProvider extends ConfigProvider {
    public String getConfigPath() {
        // TODO get current dir
        return ConfigProvider.class.getClassLoader().getResource(".").getPath() + "/config";
    }

    public String getCurrentPath() {
        // TODO get current dir
        return ConfigProvider.class.getClassLoader().getResource(".").getPath() + "/data";
    }

    @Override
    public String getNetwork() {
        return "";
    }

}
