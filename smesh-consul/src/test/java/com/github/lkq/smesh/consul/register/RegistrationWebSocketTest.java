package com.github.lkq.smesh.consul.register;

import com.github.lkq.smesh.consul.client.ServiceRegistrar;
import com.github.lkq.smesh.consul.client.http.Response;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.IOException;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

class RegistrationWebSocketTest {

    public static final String SERVICE_DEFINITION = "{\"address\":\"172.17.0.4\",\"port\":8081,\"name\":\"userapp\",\"id\":\"userapp-1527783998470\"}";
    public static final String SERVICE_ID = "userapp-1527783998470";
    public static final String NORMAL_RESPONSE = "{\"status\": \"success\",\"service\":\"test-register\"}";
    private RegistrationWebSocket socket;
    @Mock
    private Session session;
    @Mock
    private ServiceRegistrar registrar;
    @Mock
    private RemoteEndpoint remote;
    @Mock
    private ResponseFactory responseFactory;
    @Mock
    private Response successResponse;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void canRegisterAndDeRegisterService() throws IOException {

        given(session.getRemote()).willReturn(remote);
        given(registrar.register(SERVICE_DEFINITION)).willReturn(successResponse);
        given(responseFactory.responseNormal(successResponse)).willReturn(NORMAL_RESPONSE);

        socket = new RegistrationWebSocket(registrar, responseFactory);
        socket.message(session, SERVICE_DEFINITION);

        verify(remote, times(1)).sendString(NORMAL_RESPONSE);

        given(registrar.deRegister(SERVICE_DEFINITION)).willReturn(successResponse);

        socket.closed(session, 400, "test");

        verify(registrar, times(1)).deRegister(SERVICE_DEFINITION);
    }

    @Test
    void registrationIsIdempotent() throws IOException {

        given(session.getRemote()).willReturn(remote);
        given(registrar.register(SERVICE_DEFINITION)).willReturn(successResponse);
        given(responseFactory.responseNormal(successResponse)).willReturn(NORMAL_RESPONSE);

        socket = new RegistrationWebSocket(registrar, responseFactory);
        socket.message(session, SERVICE_DEFINITION);
        socket.message(session, SERVICE_DEFINITION);

        verify(remote, times(2)).sendString(NORMAL_RESPONSE);
        verify(registrar, times(2)).register(SERVICE_DEFINITION);
    }

    @Test
    void willRemoveSessionAfterClose() throws IOException {
        socket = new RegistrationWebSocket(registrar, responseFactory);

        given(session.getRemote()).willReturn(remote);
        given(registrar.deRegister(SERVICE_ID)).willReturn(successResponse);
        given(responseFactory.responseNormal(successResponse)).willReturn(NORMAL_RESPONSE);

        socket.message(session, SERVICE_DEFINITION);
        socket.closed(session, 400, "");
        socket.closed(session, 400, "");

        verify(registrar, times(1)).deRegister(SERVICE_DEFINITION);
    }
}