package com.github.lkq.smesh.consul.client;

import com.github.lkq.smesh.consul.client.http.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.MockitoAnnotations.initMocks;

class ServiceRegistrarTest {
    public static final String SERVICE_ID = "userapp-1527783998470";
    public static final String SERVICE_DEFINITION = "{\"address\":\"172.17.0.4\",\"port\":8081,\"name\":\"userapp\",\"id\":\"userapp-1527783998470\"}";

    private ServiceRegistrar registrar;
    @Mock
    private ConsulClient client;
    @Mock
    private Response response;

    @BeforeEach
    void setUp() {
        initMocks(this);
        registrar = new ServiceRegistrar(client);
    }

    @Test
    void canRegisterService() {
        given(client.register(anyString())).willReturn(response);
        Response response = registrar.register(SERVICE_DEFINITION);

        assertThat(response, is(response));
    }

    @Test
    void canDeRegisterService() {
        given(client.register(SERVICE_ID)).willReturn(response);
        Response response = registrar.deRegister(SERVICE_DEFINITION);

        assertThat(response, is(response));
    }
}