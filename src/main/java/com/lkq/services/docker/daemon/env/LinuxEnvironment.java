package com.lkq.services.docker.daemon.env;

import spark.utils.StringUtils;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LinuxEnvironment implements Environment {
    @Override
    public Environment.ConsulRole consulRole() {
        String tagValue = getEnv("consul.role", "");
        if ("server".equals(tagValue)) {
            return Environment.ConsulRole.SERVER;
        }
        return Environment.ConsulRole.CLIENT;
    }

    @Override
    public List<String> clusterMembers() {
        String members = getEnv("consul.cluster.member", "");
        ArrayList<String> clusterMembers = new ArrayList<>();
        String[] split = members.split(" ");
        for (String host : split) {
            if (StringUtils.isNotEmpty(host)) {
                clusterMembers.add(host);
            }
        }
        return clusterMembers;
    }

    @Override
    public String getDataPath() {
        return Paths.get(".").toAbsolutePath().normalize().toString() + "/data";
    }

    private String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        if (StringUtils.isEmpty(value)) {
            value = System.getProperty(key, defaultValue);
        }
        return value;
    }
}
