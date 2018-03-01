package com.kliu.services.docker.daemon.consul;

import com.kliu.services.docker.daemon.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class ConsulDockerControllerTest {

    private ConsulDockerController consulDockerController;

    @BeforeEach
    void setUp() {
        String configPath = getClass().getClassLoader().getResource(".").getPath() + "/config";
        String dataPath = getClass().getClassLoader().getResource(".").getPath() + "/data";
        consulDockerController = new ConsulDockerController(configPath, dataPath);
    }

    @IntegrationTest
    @Test
    void canStartConsul() {
        boolean started = consulDockerController.startConsul();
        assertThat(started, is(true));
    }
}