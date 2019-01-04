package com.github.lkq.smesh.consul.controller;

import com.github.lkq.smesh.consul.client.ConsulClient;
import com.github.lkq.smesh.consul.profile.Profile;
import com.github.lkq.smesh.consul.profile.ProfileFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class VersionController {
    private static Logger logger = LoggerFactory.getLogger(VersionController.class);
    public static final int RETRY_INTERVAL = 1000;

    private ConsulClient consulClient;
    private String versionKey;
    private String currentVersion;

    @Inject
    public VersionController(ConsulClient consulClient, ProfileFactory profileFactory) {
        this.consulClient = consulClient;

        Profile profile = profileFactory.create();
        this.versionKey = profile.name() + "." + profile.nodeName();
        this.currentVersion = profile.version();
    }

    public void start() {
        new Thread(() -> {
            String registeredVersion = "";
            do {
                try {
                    consulClient.putKeyValue(versionKey, currentVersion);
                    registeredVersion = registeredVersion();
                    if (!currentVersion.equals(registeredVersion)) {
                        Thread.sleep(RETRY_INTERVAL);
                    }
                } catch (Exception e) {
                    logger.error("failed to register version: {}", e.getMessage());
                }
            } while (!currentVersion.equals(registeredVersion));
            logger.info("registered version: {}", registeredVersion);
        }).start();
    }

    public String registeredVersion() {
        try {
            return consulClient.getKeyValue(versionKey);
        } catch (Exception e) {
            logger.warn("failed to get registered version: {}", e.getMessage());
        }
        return "";
    }

    public String currentVersion() {
        return currentVersion;
    }
}
