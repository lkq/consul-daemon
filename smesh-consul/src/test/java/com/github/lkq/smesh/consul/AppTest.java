package com.github.lkq.smesh.consul;

import com.github.lkq.smesh.server.WebServer;
import com.github.lkq.smesh.consul.api.ConsulController;
import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.exception.ConsulDaemonException;
import com.github.lkq.smesh.consul.health.ConsulHealthChecker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

class AppTest {
    private static final String TEST_NODE_NAME = "test-node";
    private static final String APP_VERSION = "1.2.3";
    @Mock
    private ContainerContext context;
    @Mock
    private ConsulController consulController;
    @Mock
    private ConsulHealthChecker consulHealthChecker;
    @Mock
    private WebServer webServer;
    private App app;

    @BeforeEach
    void setUp() {
        initMocks(this);
        app = new App(context, consulController, consulHealthChecker, webServer, APP_VERSION);
    }

    @Test
    void willCleanStartConsulIfItsNotAlreadyRunning() {
        given(consulHealthChecker.registeredConsulDaemonVersion()).willReturn(null);
        given(consulHealthChecker.registerConsulDaemonVersion(anyString(), anyInt())).willReturn(true);
        given(consulController.startNewInstance(any(ContainerContext.class))).willReturn(true);
        given(context.nodeName()).willReturn(TEST_NODE_NAME);

        app.start(null);

        verify(consulController, times(1)).stopAndRemoveExistingInstance(TEST_NODE_NAME);
        verify(consulController, times(1)).startNewInstance(context);
        verify(consulController, times(1)).attachLogging(TEST_NODE_NAME);
        verify(consulHealthChecker, times(1)).registerConsulDaemonVersion(APP_VERSION, 10);
        verify(webServer, times(1)).start();
    }

    @Test
    void willCleanStartConsulIfDaemonVersionNotMatch() {
        given(consulHealthChecker.registeredConsulDaemonVersion()).willReturn(APP_VERSION + ".test");
        given(consulHealthChecker.registerConsulDaemonVersion(anyString(), anyInt())).willReturn(true);
        given(consulController.startNewInstance(any(ContainerContext.class))).willReturn(true);
        given(context.nodeName()).willReturn(TEST_NODE_NAME);

        app.start(null);

        verify(consulController, times(1)).stopAndRemoveExistingInstance(TEST_NODE_NAME);
        verify(consulController, times(1)).startNewInstance(context);
        verify(consulController, times(1)).attachLogging(TEST_NODE_NAME);
        verify(consulHealthChecker, times(1)).registerConsulDaemonVersion(APP_VERSION, 10);
        verify(webServer, times(1)).start();
    }

    @Test
    void willAttachConsulLogIfDaemonVersionMatch() {
        given(consulHealthChecker.registeredConsulDaemonVersion()).willReturn(APP_VERSION);
        given(consulController.startNewInstance(any(ContainerContext.class))).willReturn(true);
        given(consulHealthChecker.registerConsulDaemonVersion(anyString(), anyInt())).willReturn(true);
        given(context.nodeName()).willReturn(TEST_NODE_NAME);

        app.start(null);

        verify(consulController, never()).stopAndRemoveExistingInstance(TEST_NODE_NAME);
        verify(consulController, never()).startNewInstance(context);
        verify(consulController, times(1)).attachLogging(TEST_NODE_NAME);
        verify(consulHealthChecker, times(1)).registerConsulDaemonVersion(APP_VERSION, 10);
        verify(webServer, times(1)).start();
    }

    @Test
    void willCleanStartConsulIfToldTo() {
        given(consulHealthChecker.registeredConsulDaemonVersion()).willReturn(APP_VERSION);
        given(consulController.startNewInstance(any(ContainerContext.class))).willReturn(true);
        given(consulHealthChecker.registerConsulDaemonVersion(anyString(), anyInt())).willReturn(true);
        given(context.nodeName()).willReturn(TEST_NODE_NAME);

        app.start(true);

        verify(consulController, times(1)).stopAndRemoveExistingInstance(TEST_NODE_NAME);
        verify(consulController, times(1)).startNewInstance(context);
        verify(consulController, times(1)).attachLogging(TEST_NODE_NAME);
        verify(consulHealthChecker, times(1)).registerConsulDaemonVersion(APP_VERSION, 10);
        verify(webServer, times(1)).start();
    }

    @Test
    void willNotCleanStartConsulIfToldNotTo() {
        given(consulHealthChecker.registeredConsulDaemonVersion()).willReturn(APP_VERSION + ".test");
        given(consulController.startNewInstance(any(ContainerContext.class))).willReturn(true);
        given(consulHealthChecker.registerConsulDaemonVersion(anyString(), anyInt())).willReturn(true);
        given(context.nodeName()).willReturn(TEST_NODE_NAME);

        app.start(false);

        verify(consulController, never()).stopAndRemoveExistingInstance(TEST_NODE_NAME);
        verify(consulController, never()).startNewInstance(context);
        verify(consulController, times(1)).attachLogging(TEST_NODE_NAME);
        verify(consulHealthChecker, times(1)).registerConsulDaemonVersion(APP_VERSION, 10);
        verify(webServer, times(1)).start();
    }

    @Test
    void willThrowExceptionIfCanNotRegisterNewConsulDaemonVersion() {
        given(consulHealthChecker.registeredConsulDaemonVersion()).willReturn(APP_VERSION + ".test");
        given(consulHealthChecker.registerConsulDaemonVersion(anyString(), anyInt())).willReturn(false);
        given(consulController.startNewInstance(any(ContainerContext.class))).willReturn(true);
        given(context.nodeName()).willReturn(TEST_NODE_NAME);

        Assertions.assertThrows(ConsulDaemonException.class, () -> app.start(false), "expect failed to register new daemon version");


        verify(consulController, never()).stopAndRemoveExistingInstance(TEST_NODE_NAME);
        verify(consulController, never()).startNewInstance(context);
        verify(consulController, times(1)).attachLogging(TEST_NODE_NAME);
        verify(consulHealthChecker, times(1)).registerConsulDaemonVersion(APP_VERSION, 10);
        verify(webServer, never()).start();

    }
}