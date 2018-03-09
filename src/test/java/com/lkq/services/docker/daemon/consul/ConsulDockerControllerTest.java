//package com.kliu.services.docker.daemon.consul;
//
//import IntegrationTest;
//import TestConfigProvider;
//import Config;
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