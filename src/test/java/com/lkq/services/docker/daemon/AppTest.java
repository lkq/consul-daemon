package com.lkq.services.docker.daemon;

import com.lkq.services.docker.daemon.consul.ConsulController;
import com.lkq.services.docker.daemon.consul.context.ConsulContext;
import com.lkq.services.docker.daemon.health.ConsulHealthChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

class AppTest {
    public static final String TEST_NODE_NAME = "test-node";
    public static final String APP_VERSION = "1.2.3";
    @Mock
    private ConsulContext context;
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
        given(context.nodeName()).willReturn(TEST_NODE_NAME);

        app.start(null);

        verify(consulController, times(1)).stopAndRemoveExistingInstance(TEST_NODE_NAME);
        verify(consulController, times(1)).startNewInstance(context);
        verify(consulController, times(1)).attachLogging(TEST_NODE_NAME);
        verify(consulHealthChecker, times(1)).registerConsulDaemonVersion(APP_VERSION);
        verify(webServer, times(1)).start();
    }

    @Test
    void willCleanStartConsulIfDaemonVersionNotMatch() {
        given(consulHealthChecker.registeredConsulDaemonVersion()).willReturn(APP_VERSION + ".test");
        given(context.nodeName()).willReturn(TEST_NODE_NAME);

        app.start(null);

        verify(consulController, times(1)).stopAndRemoveExistingInstance(TEST_NODE_NAME);
        verify(consulController, times(1)).startNewInstance(context);
        verify(consulController, times(1)).attachLogging(TEST_NODE_NAME);
        verify(consulHealthChecker, times(1)).registerConsulDaemonVersion(APP_VERSION);
        verify(webServer, times(1)).start();
    }

    @Test
    void willAttachConsulLogIfDaemonVersionMatch() {
        given(consulHealthChecker.registeredConsulDaemonVersion()).willReturn(APP_VERSION);
        given(context.nodeName()).willReturn(TEST_NODE_NAME);

        app.start(null);

        verify(consulController, never()).stopAndRemoveExistingInstance(TEST_NODE_NAME);
        verify(consulController, never()).startNewInstance(context);
        verify(consulController, times(1)).attachLogging(TEST_NODE_NAME);
        verify(consulHealthChecker, times(1)).registerConsulDaemonVersion(APP_VERSION);
        verify(webServer, times(1)).start();
    }

    @Test
    void willCleanStartConsulIfToldTo() {
        given(consulHealthChecker.registeredConsulDaemonVersion()).willReturn(APP_VERSION);
        given(context.nodeName()).willReturn(TEST_NODE_NAME);

        app.start(true);

        verify(consulController, times(1)).stopAndRemoveExistingInstance(TEST_NODE_NAME);
        verify(consulController, times(1)).startNewInstance(context);
        verify(consulController, times(1)).attachLogging(TEST_NODE_NAME);
        verify(consulHealthChecker, times(1)).registerConsulDaemonVersion(APP_VERSION);
        verify(webServer, times(1)).start();
    }

    @Test
    void willNotCleanStartConsulIfToldNotTo() {
        given(consulHealthChecker.registeredConsulDaemonVersion()).willReturn(APP_VERSION + ".test");
        given(context.nodeName()).willReturn(TEST_NODE_NAME);

        app.start(false);

        verify(consulController, never()).stopAndRemoveExistingInstance(TEST_NODE_NAME);
        verify(consulController, never()).startNewInstance(context);
        verify(consulController, times(1)).attachLogging(TEST_NODE_NAME);
        verify(consulHealthChecker, times(1)).registerConsulDaemonVersion(APP_VERSION);
        verify(webServer, times(1)).start();
    }
}