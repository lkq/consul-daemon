package com.github.lkq.smesh.smesh4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.net.URI;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


class SmeshTest {

    public static final String SERVICE_DEFINITION = "abc";
    public static final String URI1 = "ws://localhost:1234";
    public static final String URI2 = "ws://localhost:1235";
    public static final String URI3 = "ws://localhost:1236";
    @Mock
    private WebSocketClientFactory webSocketFactory;
    @Mock
    private WebSocketClient webSocketClient;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void willRegisterWithFirstURIOnRegisterCalled() throws InterruptedException {
        given(webSocketFactory.create(any(URI.class), eq(SERVICE_DEFINITION), any(ReconnectListener.class))).willReturn(webSocketClient);
        Smesh smesh = Smesh.register(new String[]{URI1, URI2}, SERVICE_DEFINITION, webSocketFactory, 1);
        Thread.sleep(100);

        verify(webSocketClient, times(1)).start();
        verify(webSocketFactory, times(1)).create(URI.create(URI1), SERVICE_DEFINITION, smesh);
    }

    @Test
    void willRetryWithAllURIs() throws InterruptedException {
        given(webSocketFactory.create(any(URI.class), eq(SERVICE_DEFINITION), any(ReconnectListener.class))).willReturn(webSocketClient);
        Smesh smesh = Smesh.register(new String[]{URI1, URI2, URI3}, SERVICE_DEFINITION, webSocketFactory, 100);
        Thread.sleep(50);

        verify(webSocketClient, times(1)).start();
        verify(webSocketFactory, times(1)).create(URI.create(URI1), SERVICE_DEFINITION, smesh);

        smesh.onDisconnect();
        Thread.sleep(100);

        verify(webSocketClient, times(2)).start();
        verify(webSocketFactory, times(1)).create(URI.create(URI2), SERVICE_DEFINITION, smesh);

        smesh.onDisconnect();
        Thread.sleep(100);

        verify(webSocketClient, times(3)).start();
        verify(webSocketFactory, times(1)).create(URI.create(URI3), SERVICE_DEFINITION, smesh);

        smesh.onDisconnect();
        Thread.sleep(100);

        verify(webSocketClient, times(4)).start();
        verify(webSocketFactory, times(2)).create(URI.create(URI1), SERVICE_DEFINITION, smesh);
    }

    @Test
    void willNotRetryAfterDeRegistered() throws InterruptedException {
        given(webSocketFactory.create(any(URI.class), eq(SERVICE_DEFINITION), any(ReconnectListener.class))).willReturn(webSocketClient);
        Smesh smesh = Smesh.register(new String[]{URI1, URI2}, SERVICE_DEFINITION, webSocketFactory, 100);
        Thread.sleep(50);

        verify(webSocketClient, times(1)).start();
        verify(webSocketFactory, times(1)).create(URI.create(URI1), SERVICE_DEFINITION, smesh);

        smesh.onDisconnect();
        smesh.deRegister();
        Thread.sleep(100);

        verify(webSocketClient, times(1)).start();
        verify(webSocketFactory, never()).create(URI.create(URI2), SERVICE_DEFINITION, smesh);

    }

    @Test
    void willNotRetryAfterRegistered() throws InterruptedException {
        given(webSocketFactory.create(any(URI.class), eq(SERVICE_DEFINITION), any(ReconnectListener.class))).willReturn(webSocketClient);
        Smesh smesh = Smesh.register(new String[]{URI1, URI2}, SERVICE_DEFINITION, webSocketFactory, 100);
        Thread.sleep(50);

        verify(webSocketClient, times(1)).start();
        verify(webSocketFactory, times(1)).create(URI.create(URI1), SERVICE_DEFINITION, smesh);

        Thread.sleep(100);

        verify(webSocketClient, times(1)).start();
        verify(webSocketFactory, never()).create(URI.create(URI2), SERVICE_DEFINITION, smesh);

    }

    @Test
    void willNotRetryWithinInterval() throws InterruptedException {
        given(webSocketFactory.create(any(URI.class), eq(SERVICE_DEFINITION), any(ReconnectListener.class))).willReturn(webSocketClient);
        Smesh smesh = Smesh.register(new String[]{URI1, URI2}, SERVICE_DEFINITION, webSocketFactory, 100);
        Thread.sleep(10);

        verify(webSocketClient, times(1)).start();
        verify(webSocketFactory, times(1)).create(URI.create(URI1), SERVICE_DEFINITION, smesh);

        smesh.onDisconnect();

        Thread.sleep(50);

        verify(webSocketClient, times(1)).start();
        verify(webSocketFactory, never()).create(URI.create(URI2), SERVICE_DEFINITION, smesh);

    }
}