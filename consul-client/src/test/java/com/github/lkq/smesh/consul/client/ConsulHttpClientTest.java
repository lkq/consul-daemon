package com.github.lkq.smesh.consul.client;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static spark.Spark.*;

class ConsulHttpClientTest {

    private ConsulHttpClient client = new ConsulHttpClient();
    private static int port;

    @BeforeAll
    static void startMockServer() {
        port(0);
        get("test/v1/name", (req, res) -> "test-name");
        put("test/v1/name", (req, res) -> "put " + req.body());
        post("test/v1/name", (req, res) -> "post " + req.body());
        awaitInitialization();
        port = port();
    }

    @Test
    void canGet() {
        String name = client.get("http://localhost:" + port + "/test/v1/name");
        assertThat(name, CoreMatchers.is("test-name"));
    }

    @Test
    void canPut() {
        String name = client.put("http://localhost:" + port + "/test/v1/name", "some data");
        assertThat(name, CoreMatchers.is("put some data"));
    }

    @Test
    void canPost() {
        String name = client.post("http://localhost:" + port + "/test/v1/name", "some data");
        assertThat(name, CoreMatchers.is("post some data"));
    }
}