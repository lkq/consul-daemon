package com.github.lkq.smesh.consul.register;

import com.github.lkq.smesh.consul.client.ConsulClient;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.inject.Provider;
import java.io.IOException;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

class RegistrationWebSocketTest {

    public static final String REQUEST_BODY = "test-register";
    public static final String SUCCESS_RESPONSE = "{\"status\": \"success\",\"service\":\"test-register\"}";
    public static final String FAIL_RESPONSE = "{\"status\": \"fail\",\"reason\":\"not connected\",\"service\":\"test-register\"}";
    private RegistrationWebSocket socket;
    @Mock
    private ConsulClient client;
    @Mock
    private Session session;
    @Mock
    private ConsulRegistrar registrar;
    @Mock
    private RemoteEndpoint remote;
    @Mock
    private ResponseFactory responseFactory;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void canRegisterAndDeRegisterService() throws IOException {

        given(registrar.register(REQUEST_BODY)).willReturn(SUCCESS_RESPONSE);
        given(session.getRemote()).willReturn(remote);
        given(responseFactory.responseSuccess(anyString())).willReturn(SUCCESS_RESPONSE);

        socket = new RegistrationWebSocket(client, () -> registrar, responseFactory);
        socket.connected(session);
        socket.message(session, REQUEST_BODY);

        verify(registrar, times(1)).register(REQUEST_BODY);
        verify(remote, times(1)).sendString(SUCCESS_RESPONSE);
    }

    @Test
    void willIgnoreDuplicateConnection() throws IOException {

        given(registrar.register(REQUEST_BODY)).willReturn(SUCCESS_RESPONSE);
        given(responseFactory.responseSuccess(anyString())).willReturn(SUCCESS_RESPONSE);

        Provider<ConsulRegistrar> provider = mock(Provider.class);
        given(provider.get()).willReturn(registrar);

        socket = new RegistrationWebSocket(client, provider, responseFactory);
        socket.connected(session);
        socket.connected(session);

        verify(provider, times(1)).get();
    }

    @Test
    void canDeRegisterServiceOnSessionClose() throws IOException {
        socket = new RegistrationWebSocket(client, () -> registrar, responseFactory);
        socket.connected(session);
        socket.closed(session, 400, "test-deregister");

        verify(registrar, times(1)).deRegister();
    }

    @Test
    void willRemoveSessionAfterClose() throws IOException {
        socket = new RegistrationWebSocket(client, () -> registrar, responseFactory);
        socket.connected(session);
        socket.closed(session, 400, "test-deregister");
        socket.closed(session, 400, "test-deregister");

        verify(registrar, times(1)).deRegister();
    }

    @Test
    void canHandleMessageAfterSessionClose() throws IOException {
        given(session.getRemote()).willReturn(remote);
        given(responseFactory.responseFail(anyString(), anyString())).willReturn(FAIL_RESPONSE);

        socket = new RegistrationWebSocket(client, () -> registrar, responseFactory);
        socket.connected(session);
        socket.closed(session, 400, "test-deregister");

        socket.message(session, REQUEST_BODY);

        verify(registrar, times(1)).deRegister();
        verify(remote, times(1)).sendString(FAIL_RESPONSE);
    }
}