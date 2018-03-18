package com.lkq.services.docker.daemon.consul;

import spark.utils.StringUtils;

import java.util.Base64;

/**
 * check the local consul instance healthiness by putting a kv pair and query back
 */
public class ConsulHealthChecker {

    private final ConsulAPI consulAPI;
    private final String daemonVersionKey;
    private final String daemonVersion;

    public ConsulHealthChecker(ConsulAPI consulAPI, String nodeName, String daemonVersion) {
        this.consulAPI = consulAPI;
        this.daemonVersionKey = nodeName + "-daemon-version";
        this.daemonVersion = daemonVersion;
    }

    public boolean isHealthy() {
        String expectedVersionTag = createVersionTimestamp(daemonVersion);
        consulAPI.putKeyValue(daemonVersionKey, expectedVersionTag);
        String currentVersion = getCurrentDaemonVersion();
        return currentVersion.equals(expectedVersionTag);
    }

    public String getCurrentDaemonVersion() {
        String encodedValue = consulAPI.getKeyValue(daemonVersionKey);
        if (StringUtils.isNotEmpty(encodedValue)) {
            return new String(Base64.getDecoder().decode(encodedValue.getBytes()));
        } else {
            return null;
        }
    }

    public void registerDaemonVersion(String jarVersion) {
        consulAPI.putKeyValue(daemonVersionKey, createVersionTimestamp(jarVersion));
    }

    private String createVersionTimestamp(String jarVersion) {
        return jarVersion + "@" + System.currentTimeMillis();
    }
}
