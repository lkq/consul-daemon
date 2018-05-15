package com.github.lkq.smesh.consul.client;

import com.github.lkq.smesh.consul.client.http.Response;
import com.github.lkq.smesh.consul.client.http.SimpleHttpClient;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

class ConsulClientTest {
    @Mock
    private SimpleHttpClient httpClient;
    @Mock
    private ResponseParser parser;
    @Mock
    private Response response;
    private ConsulClient client;

    @BeforeEach
    void setUp() {
        initMocks(this);
        this.client = new ConsulClient(httpClient, parser, 1024);
    }

    @Test
    void canGetKeyValue() {
        given(httpClient.get("http://localhost:1024/v1/kv/test-key")).willReturn(new Response(200, "any"));
        Map<String, String> res = new HashMap<>();
        res.put("Value", "dGVzdC12YWx1ZQ");
        given(parser.parse("any")).willReturn(res);

        String value = client.getKeyValue("test-key");
        assertThat(value, CoreMatchers.is("test-value"));
    }

    @Test
    void canPutKeyValue() {
        given(httpClient.put("http://localhost:1024/v1/kv/test-key", "test-value")).willReturn(response);
        given(response.status()).willReturn(200);
        given(response.body()).willReturn("true");
        boolean success = client.putKeyValue("test-key", "test-value");

        assertTrue(success);
        verify(httpClient, times(1)).put("http://localhost:1024/v1/kv/test-key", "test-value");
    }
}