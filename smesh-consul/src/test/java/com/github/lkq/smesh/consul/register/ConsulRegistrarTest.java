package com.github.lkq.smesh.consul.register;

import com.github.lkq.smesh.consul.client.ConsulClient;
import com.github.lkq.smesh.consul.client.http.Response;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

class ConsulRegistrarTest {

    private ConsulRegistrar registrar;
    @Mock
    private ConsulClient client;
    @Mock
    private Response response;

    @BeforeEach
    void setUp() {
        initMocks(this);
        registrar = new ConsulRegistrar(client);
    }

    @Test
    void canRegisterService() {
        given(client.register("service")).willReturn(response);
        given(response.body()).willReturn("success");
        String resp = registrar.register("service");

        assertThat(resp, CoreMatchers.is("success"));
    }
}