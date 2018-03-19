package com.lkq.services.docker.daemon.health;

import com.lkq.services.docker.daemon.consul.ConsulAPI;
import spark.Request;
import spark.Response;

import java.util.Map;

/**
 * check the local consul instance healthiness by putting a kv pair and query back
 */
public class ConsulHealthChecker {

    private final ConsulAPI consulAPI;
    private final String nodeName;
    private final String daemonVersionKey;
    private final String daemonVersion;

    public ConsulHealthChecker(ConsulAPI consulAPI, String nodeName, String daemonVersion) {
        this.consulAPI = consulAPI;
        this.nodeName = nodeName;
        this.daemonVersionKey = nodeName + "-daemon-version";
        this.daemonVersion = daemonVersion;
    }

    public String getNodeHealth(Request request, Response response) {
        String full = request.queryParams("full");
        Map<String, String> nodeHealth = consulAPI.getNodeHealth(nodeName);
        if (full == null) {
            String status = nodeHealth.get("Status");
            return String.valueOf("passing".equals(status));
        }
        return "false";
    }

    public boolean isHealthy() {
        String expectedVersionTag = createVersionTimestamp(daemonVersion);
        consulAPI.putKeyValue(daemonVersionKey, expectedVersionTag);
        String currentVersion = registeredConsulDaemonVersion();
        return currentVersion.equals(expectedVersionTag);
    }

    public String registeredConsulDaemonVersion() {
        return consulAPI.getKeyValue(daemonVersionKey);
    }

    public void registerConsulDaemonVersion(String appVersion) {
        consulAPI.putKeyValue(daemonVersionKey, createVersionTimestamp(appVersion));
    }

    private String createVersionTimestamp(String jarVersion) {
        return jarVersion + "@" + System.currentTimeMillis();
    }
}
