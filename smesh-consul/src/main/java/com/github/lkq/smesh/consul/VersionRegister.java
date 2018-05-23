package com.github.lkq.smesh.consul;

import com.github.lkq.smesh.consul.client.ConsulClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionRegister {
    private static Logger logger = LoggerFactory.getLogger(VersionRegister.class);

    private final int interval;
    private ConsulClient consulClient;
    private String versionKey;
    private String expectedVersion;

    public VersionRegister(ConsulClient consulClient, String versionKey, String expectedVersion, int intervalInMs) {
        this.consulClient = consulClient;
        this.versionKey = versionKey;
        this.expectedVersion = expectedVersion;
        this.interval = intervalInMs;
    }

    public void registerVersion() {
        new Thread(() -> {
            String registeredVersion = "";
            do {
                delay(interval);
                try {
                    consulClient.putKeyValue(versionKey, expectedVersion);
                    registeredVersion = registeredVersion();

                } catch (Exception e) {
                    logger.error("failed to register version: {}", e.getMessage());
                }

            } while (!expectedVersion.equals(registeredVersion));
            logger.info("registered version: {}", registeredVersion);
        }).start();
    }

    private void delay(int interval) {
        try {
            if (interval > 0) {
                Thread.sleep(interval);
            }
        } catch (InterruptedException ignored) {
        }
    }

    public String registeredVersion() {
        try {
            return consulClient.getKeyValue(versionKey);
        } catch (Exception e) {
            logger.warn("failed to get registered version: {}", e.getMessage());
        }
        return "";
    }

    public String expectedVersion() {
        return expectedVersion;
    }
}
