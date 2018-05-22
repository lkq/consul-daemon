package com.github.lkq.smesh.consul.health;

import com.github.lkq.smesh.consul.client.ConsulClient;
import spark.Request;
import spark.Response;
import spark.utils.StringUtils;

import java.util.Map;

/**
 * check the local consul instance healthiness by putting a kv pair and query back
 */
public class ConsulHealthChecker {

    private final ConsulClient consulClient;
    private final String nodeName;
    private final String daemonVersionKey;
    private final String daemonVersion;

    public ConsulHealthChecker(ConsulClient consulClient, String nodeName, String daemonVersion) {
        this.consulClient = consulClient;
        this.nodeName = nodeName;
        this.daemonVersionKey = "consul-version-" + nodeName;
        this.daemonVersion = daemonVersion;
    }

    public String getNodeHealth(Request request, Response response) {
        String full = request.queryParams("full");
        Map<String, String> nodeHealth = consulClient.getNodeHealth(nodeName);
        if (full == null) {
            String status = nodeHealth.get("Status");
            return String.valueOf("passing".equals(status));
        }
        return "false";
    }

    public boolean isHealthy() {
        String expectedVersionTag = createVersionTimestamp(daemonVersion);
        consulClient.putKeyValue(daemonVersionKey, expectedVersionTag);
        String currentVersion = registeredConsulDaemonVersion();
        return currentVersion.equals(expectedVersionTag);
    }

    public String registeredConsulDaemonVersion() {
        return consulClient.getKeyValue(daemonVersionKey);
    }

    public boolean registerConsulDaemonVersion(String appVersion, int timeoutInSeconds) {
        boolean success = false;
        do {
            try {
                consulClient.putKeyValue(daemonVersionKey, createVersionTimestamp(appVersion));
                String version = registeredConsulDaemonVersion();
                if (StringUtils.isNotEmpty(version)) {
                    success = appVersion.equals(version.split("@")[0]);
                }
                if (!success && timeoutInSeconds > 0) {
                    timeoutInSeconds -= 1;
                    Thread.sleep(1000);
                }
            } catch (Exception ignored) {
            }
        } while (!success && timeoutInSeconds > 0);
        return success;
    }

    private String createVersionTimestamp(String jarVersion) {
        return jarVersion + "@" + System.currentTimeMillis();
    }
}
