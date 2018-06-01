package com.github.lkq.smesh.consul.register;

import com.github.lkq.smesh.consul.client.ConsulClient;
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

    public static final String REQUEST_BODY = "{\"address\":\"172.17.0.4\",\"port\":8081,\"name\":\"userapp\",\"id\":\"userapp-1527783998470\"}";
    public static final String SERVICE_ID = "userapp-1527783998470";
    public static final String SUCCESS_RESPONSE = "{\"status\": \"success\",\"service\":\"test-register\"}";
    public static final String FAIL_RESPONSE = "{\"status\": \"fail\",\"reason\":\"not connected\",\"service\":\"test-register\"}";
    private RegistrationWebSocket socket;
    @Mock
    private ConsulClient client;
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
        given(responseFactory.responseNormal(successResponse)).willReturn(SUCCESS_RESPONSE);
        given(client.register(REQUEST_BODY)).willReturn(successResponse);
        given(successResponse.body()).willReturn(SUCCESS_RESPONSE);

        socket = new RegistrationWebSocket(client, responseFactory);
        socket.connected(session);
        socket.message(session, REQUEST_BODY);

        verify(remote, times(1)).sendString(SUCCESS_RESPONSE);

        given(client.deregister(REQUEST_BODY)).willReturn(successResponse);

        socket.closed(session, 400, "test");

        verify(client, times(1)).deregister(SERVICE_ID);

    }

    @Test
    void registrationIsIdempotent() throws IOException {

        given(successResponse.body()).willReturn(SUCCESS_RESPONSE);
        given(responseFactory.responseNormal(successResponse)).willReturn(SUCCESS_RESPONSE);
        given(session.getRemote()).willReturn(remote);
        given(client.register(REQUEST_BODY)).willReturn(successResponse);
        given(successResponse.body()).willReturn("success");

        socket = new RegistrationWebSocket(client, responseFactory);
        socket.message(session, REQUEST_BODY);
        socket.message(session, REQUEST_BODY);

        verify(remote, times(2)).sendString("{\"status\": \"success\",\"service\":\"test-register\"}");
        verify(client, times(2)).register(REQUEST_BODY);
    }

    @Test
    void willRemoveSessionAfterClose() throws IOException {
        socket = new RegistrationWebSocket(client, responseFactory);

        given(client.deregister(SERVICE_ID)).willReturn(successResponse);
        given(successResponse.body()).willReturn("success");
        given(session.getRemote()).willReturn(remote);

        socket.message(session, REQUEST_BODY);
        socket.closed(session, 400, "");
        socket.closed(session, 400, "");

        verify(client, times(1)).deregister(SERVICE_ID);
    }
}