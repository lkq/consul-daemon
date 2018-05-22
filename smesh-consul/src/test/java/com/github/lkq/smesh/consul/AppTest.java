package com.github.lkq.smesh.consul;

import com.github.lkq.smesh.consul.container.ConsulController;
import com.github.lkq.smesh.consul.health.ConsulHealthChecker;
import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.server.WebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
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
    @Mock
    private VersionRegister versionRegister;
    private App app;

    @BeforeEach
    void setUp() {
        initMocks(this);
        app = new App(context, consulController, consulHealthChecker, versionRegister, webServer, APP_VERSION);
    }

    @Test
    void willCleanStartConsulIfItsNotAlreadyRunning() {
        given(versionRegister.registeredVersion()).willReturn(null);
        given(consulController.startNewInstance(any(ContainerContext.class))).willReturn(true);
        given(context.nodeName()).willReturn(TEST_NODE_NAME);

        app.start(null);

        verify(consulController, times(1)).stopAndRemoveExistingInstance(TEST_NODE_NAME);
        verify(consulController, times(1)).startNewInstance(context);
        verify(consulController, times(1)).attachLogging(TEST_NODE_NAME);
        verify(webServer, times(1)).start();
    }

    @Test
    void willCleanStartConsulIfDaemonVersionNotMatch() {
        given(versionRegister.registeredVersion()).willReturn(APP_VERSION + ".test");
        given(consulController.startNewInstance(any(ContainerContext.class))).willReturn(true);
        given(context.nodeName()).willReturn(TEST_NODE_NAME);

        app.start(null);

        verify(consulController, times(1)).stopAndRemoveExistingInstance(TEST_NODE_NAME);
        verify(consulController, times(1)).startNewInstance(context);
        verify(consulController, times(1)).attachLogging(TEST_NODE_NAME);
        verify(webServer, times(1)).start();
    }

    @Test
    void willAttachConsulLogIfDaemonVersionMatch() {
        given(consulController.startNewInstance(any(ContainerContext.class))).willReturn(true);
        given(context.nodeName()).willReturn(TEST_NODE_NAME);
        given(versionRegister.expectedVersion()).willReturn(APP_VERSION);
        given(versionRegister.registeredVersion()).willReturn(APP_VERSION);

        app.start(null);

        verify(consulController, never()).stopAndRemoveExistingInstance(TEST_NODE_NAME);
        verify(consulController, never()).startNewInstance(context);
        verify(consulController, times(1)).attachLogging(TEST_NODE_NAME);
        verify(webServer, times(1)).start();
    }

    @Test
    void willCleanStartConsulIfToldTo() {
        given(versionRegister.registeredVersion()).willReturn(APP_VERSION);
        given(consulController.startNewInstance(any(ContainerContext.class))).willReturn(true);
        given(context.nodeName()).willReturn(TEST_NODE_NAME);

        app.start(true);

        verify(consulController, times(1)).stopAndRemoveExistingInstance(TEST_NODE_NAME);
        verify(consulController, times(1)).startNewInstance(context);
        verify(consulController, times(1)).attachLogging(TEST_NODE_NAME);
        verify(webServer, times(1)).start();
    }

    @Test
    void willNotCleanStartConsulIfToldNotTo() {
        given(versionRegister.registeredVersion()).willReturn(APP_VERSION + ".test");
        given(consulController.startNewInstance(any(ContainerContext.class))).willReturn(true);
        given(context.nodeName()).willReturn(TEST_NODE_NAME);

        app.start(false);

        verify(consulController, never()).stopAndRemoveExistingInstance(TEST_NODE_NAME);
        verify(consulController, never()).startNewInstance(context);
        verify(consulController, times(1)).attachLogging(TEST_NODE_NAME);
        verify(webServer, times(1)).start();
    }
}