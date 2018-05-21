package com.github.lkq.smesh.test;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;

@Disabled
public class SmeshIntegrationTest {

    private static TestEngine testEngine = new TestEngine();

    @BeforeAll
    static void setUp() throws IOException, InterruptedException {
        testEngine.startEverything();

    }

    @Test
    void canRouteToTheCorrectServiceViaSmesh() throws InterruptedException, IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://localhost:8080/userapp/users/kingson").get().build();
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
        assertThat(response.code(), CoreMatchers.is(200));
    }
}
