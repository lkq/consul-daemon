//package com.kliu.services.docker.daemon.consul;
//
//import com.kliu.services.docker.daemon.IntegrationTest;
//import com.kliu.services.docker.daemon.TestConfigProvider;
//import com.kliu.services.docker.daemon.config.Config;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.hamcrest.CoreMatchers.is;
//import static org.hamcrest.MatcherAssert.assertThat;
//
//class ConsulDockerControllerTest {
//
//    private ConsulDockerController consulDockerController;
//
//    @BeforeEach
//    void setUp() {
//        Config.init(new TestConfigProvider());
//        consulDockerController = new ConsulDockerController();
//    }
//
//    @IntegrationTest
//    @Test
//    void canStartConsul() {
//        boolean started = consulDockerController.startConsul();
//        assertThat(started, is(true));
//    }
//}