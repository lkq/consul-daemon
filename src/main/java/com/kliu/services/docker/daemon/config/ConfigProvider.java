package com.kliu.services.docker.daemon.config;

import spark.utils.StringUtils;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ConfigProvider {

    private static final String NET_EASY_HUB = "http://hub-mirror.c.163.com";

    public String getRegistryURL() {
        return getEnv("docker-registry", NET_EASY_HUB);
    }

    public String getConfigPath() {
        return Paths.get(".").toAbsolutePath().normalize().toString() + "/config";
    }

    public String getDataPath() {
        return Paths.get(".").toAbsolutePath().normalize().toString() + "/data";
    }

    public String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        if (StringUtils.isEmpty(value)) {
            value = System.getProperty(key, defaultValue);
        }
        return value;
    }

    public String getNetwork() {
        return "host";
    }

    public String getBootstrapCount() {
        return "3";
    }

    public List<String> getRetryJoin() {
        String hosts = getEnv("consul-cluster-server", "");
        String[] hostsArray = hosts.split(",");
        List<String> hostList = new ArrayList<>();
        for (String host : hostsArray) {
            hostList.add(host);
        }

        return hostList;
    }

    public int[] getTCPPort() {
        return new int[]{8300, 8301, 8302, 8400, 8500};
    }

    public int[] getUDPPorts() {
        return new int[]{8301, 8302};
    }
}
