package com.lkq.services.docker.daemon.env;

import spark.utils.StringUtils;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LinuxEnvironment implements Environment {

    @Override
    public Environment.ConsulRole consulRole() {
        String tagValue = getEnv(ENV_CONSUL_ROLE, "");
        if ("server".equals(tagValue)) {
            return Environment.ConsulRole.SERVER;
        }
        return Environment.ConsulRole.CLIENT;
    }

    @Override
    public List<String> clusterMembers() {
        String members = getEnv(ENV_CONSUL_CLUSTER_MEMBER, "");
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

    @Override
    public String getNetwork() {
        return "host";
    }

    private String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        if (StringUtils.isEmpty(value)) {
            value = System.getProperty(key, defaultValue);
        }
        return value;
    }

    @Override
    public String nodeName() {
        return getEnv(Environment.ENV_NODE_NAME, "consul_node_" + System.currentTimeMillis());
    }
}
