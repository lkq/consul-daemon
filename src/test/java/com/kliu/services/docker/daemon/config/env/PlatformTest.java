package com.kliu.services.docker.daemon.config.env;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PlatformTest {
    private static Logger logger = LoggerFactory.getLogger(PlatformTest.class);

    @Test
    void canGetPlatform() {
        Platform platform = Platform.get();
        logger.info("running in platform {}", platform);
    }

    @Test
    void canGetMacCluster() {
        System.setProperty("local.cluster", "");
        Platform platform = Platform.get();
        logger.info("running in platform {}", platform);
    }
}