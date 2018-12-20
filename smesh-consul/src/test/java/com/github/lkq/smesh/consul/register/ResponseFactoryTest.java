package com.github.lkq.smesh.consul.register;

import com.github.lkq.smesh.consul.client.http.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

class ResponseFactoryTest {

    private ResponseFactory responseFactory;
    @Mock
    private Response response;

    @BeforeEach
    void setUp() {
        initMocks(this);
        responseFactory = new ResponseFactory();
    }

    @Test
    void canResponseSuccess() {
        given(response.status()).willReturn(200);
        given(response.body()).willReturn("success body");
        String res = responseFactory.responseNormal(response);
        Assertions.assertEquals("{\"status\":\"success\",\"code\":200,\"message\":\"success body\"}", res);
    }

    @Test
    void canResponseFail() {
        given(response.status()).willReturn(400);
        given(response.body()).willReturn("fail body");
        String res = responseFactory.responseNormal(response);
        Assertions.assertEquals("{\"status\":\"fail\",\"code\":400,\"message\":\"fail body\"}", res);
    }

    @Test
    void canResponseError() {
        String res = responseFactory.responseError("fail-reason", "test-message");
        Assertions.assertEquals("{\"status\":\"error\",\"reason\":\"fail-reason\",\"message\":\"test-message\"}", res);
    }
}