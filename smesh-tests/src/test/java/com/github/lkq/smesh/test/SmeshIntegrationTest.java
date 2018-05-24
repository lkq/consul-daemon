package com.github.lkq.smesh.test;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Disabled
public class SmeshIntegrationTest {

    private static TestEngine testEngine = new TestEngine();

    @BeforeAll
    static void setUp() throws IOException, InterruptedException {
        testEngine.startEverything();
    }

    @AfterAll
    static void tearDown() {
        testEngine.stopEverything();
    }

    @Test
    void canRouteToTheCorrectServiceViaSmesh() throws InterruptedException, IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://localhost:8080/userapp/users/kingson").get().build();

        int retry = 5;
        Response response = null;
        do {
            try {
                response = client.newCall(request).execute();
                retry--;
            } catch (Exception ignored) {
                Thread.sleep(3000);
            }
        } while (retry > 0 && response == null);
        System.out.println(response.body().string());
        assertNotNull(response);
        assertThat(response.code(), CoreMatchers.is(200));
    }
}
