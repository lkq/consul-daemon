package com.kliu.services.docker.daemon;

import com.kliu.services.docker.daemon.config.ConfigProvider;

import java.util.Arrays;
import java.util.List;

public class TestConfigProvider extends ConfigProvider {
    public String getConfigPath() {
        // TODO get current dir
        return ConfigProvider.class.getClassLoader().getResource(".").getPath() + "/config";
    }

    public String getDataPath() {
        // TODO get current dir
        return ConfigProvider.class.getClassLoader().getResource(".").getPath() + "/data";
    }

    @Override
    public String getNetwork() {
        return "bridge";
    }

    public List<String> getRetryJoin() {
        return Arrays.asList("127.0.0.2");
    }
}
