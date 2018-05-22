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
            String registeredVersion = registeredVersion();
            while (!expectedVersion.equals(registeredVersion)) {
                consulClient.putKeyValue(versionKey, expectedVersion);
                registeredVersion = registeredVersion();

                try {
                    if (interval > 0) {
                        Thread.sleep(interval);
                    }
                } catch (InterruptedException ignored) { }
            }
            logger.info("registered version: {}", registeredVersion);
        }).start();
    }

    public String registeredVersion() {
        return consulClient.getKeyValue(versionKey);
    }

    public String expectedVersion() {
        return expectedVersion;
    }
}
