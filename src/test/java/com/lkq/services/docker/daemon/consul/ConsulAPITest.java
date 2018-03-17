package com.lkq.services.docker.daemon.consul;

import org.eclipse.jetty.client.HttpClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Service;

import java.io.IOException;
import java.net.ServerSocket;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

class ConsulAPITest {
    private static int apiServerPort;
    private ConsulAPI consulAPI;
    private HttpClient httpClient;
    private static Service apiServer;

    @BeforeAll
    static void startTestServer() {
        apiServer = Service.ignite();
        apiServerPort = getTestPort();
        apiServer.port(apiServerPort);
        apiServer.put("/v1/kv/test-key", (req, res) -> "true");
        apiServer.get("/v1/kv/test-key", (req, res) -> "test-value");
        apiServer.init();
    }

    private static int getTestPort() {
        try (ServerSocket s = new ServerSocket(0)) {
            return s.getLocalPort();
        } catch (IOException e) {
            return 50000;
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        initMocks(this);
        HttpClient client = new HttpClient();
        client.start();
        consulAPI = new ConsulAPI(client, apiServerPort);
    }

    @Test
    void canPutKeyValue() {
        assertTrue(consulAPI.putKeyValue("test-key", "test-value"));
    }

    @Test
    void canGetKeyValue() {
        String value = consulAPI.getKeyValue("test-key");
        assertThat(value, is("test-value"));
    }
}