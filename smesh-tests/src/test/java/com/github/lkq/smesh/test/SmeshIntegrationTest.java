package com.github.lkq.smesh.test;

import com.github.lkq.smesh.logging.JulToSlf4jBridge;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SmeshIntegrationTest {
    private static Logger logger = LoggerFactory.getLogger(SmeshIntegrationTest.class);

    @BeforeAll
    static void setUp() throws IOException, InterruptedException {
        JulToSlf4jBridge.setup();
        TestEngine.get().startEverything();
    }

    @AfterAll
    static void tearDown() {
        TestEngine.get().stopEverything();
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
        logger.info(response.body().string());
        assertNotNull(response);
        assertThat(response.code(), CoreMatchers.is(200));
    }
}
