package com.github.lkq.smesh.consul.client.http;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static spark.Spark.*;

class SimpleHttpClientTest {

    private SimpleHttpClient client = new SimpleHttpClient();
    private static int port;

    @BeforeAll
    static void startMockServer() {
        port(0);
        get("test/v1/name", (req, res) -> "test-name");
        get("test/v1/empty-body", (req, res) -> "");
        get("test/v1/not-found", (req, res) -> {res.status(404); return "";});
        put("test/v1/name", (req, res) -> "put " + req.body());
        put("test/v1/not-authorized", (req, res) -> {res.status(401);return "";});
        post("test/v1/name", (req, res) -> "post " + req.body());
        post("test/v1/forbidden", (req, res) -> {res.status(403); return "";});
        awaitInitialization();
        port = port();
    }

    @Test
    void getWithResponseBody() {
        Response res = client.get("http://localhost:" + port + "/test/v1/name");
        assertThat(res.status(), CoreMatchers.is(200));
        assertThat(res.body(), CoreMatchers.is("test-name"));
    }

    @Test
    void getWithoutResponseBody() {
        Response res = client.get("http://localhost:" + port + "/test/v1/empty-body");
        assertThat(res.status(), CoreMatchers.is(200));
        assertThat(res.body(), CoreMatchers.is(""));
    }

    @Test
    void getWithNotFound() {
        Response res = client.get("http://localhost:" + port + "/test/v1/not-found");
        assertThat(res.status(), CoreMatchers.is(404));
        assertThat(res.body(), CoreMatchers.is(""));
    }

    @Test
    void putWithResponseBody() {
        Response res = client.put("http://localhost:" + port + "/test/v1/name", "some data");
        assertThat(res.status(), CoreMatchers.is(200));
        assertThat(res.body(), CoreMatchers.is("put some data"));
    }

    @Test
    void putWithNotAuthorized() {
        Response res = client.put("http://localhost:" + port + "/test/v1/not-authorized", "some data");
        assertThat(res.status(), CoreMatchers.is(401));
        assertThat(res.body(), CoreMatchers.is(""));
    }

    @Test
    void postWithResponseBody() {
        Response res = client.post("http://localhost:" + port + "/test/v1/name", "some data");
        assertThat(res.status(), CoreMatchers.is(200));
        assertThat(res.body(), CoreMatchers.is("post some data"));
    }

    @Test
    void postWithForbidden() {
        Response res = client.post("http://localhost:" + port + "/test/v1/forbidden", "some data");
        assertThat(res.status(), CoreMatchers.is(403));
        assertThat(res.body(), CoreMatchers.is(""));
    }
}