package com.github.lkq.smesh.test.benchmark;

import com.github.lkq.smesh.Retry;
import com.github.lkq.smesh.test.TestEngine;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class BenchmarkTest {
    private static Logger logger = LoggerFactory.getLogger(BenchmarkTest.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        TestEngine.get().startEverything();
        BenchmarkTest benchmark = new BenchmarkTest();
        benchmark.runDefaultTests();
        TestEngine.get().stopEverything();
        System.exit(0);
    }
;
    void runDefaultTests() throws IOException {
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().get().url("http://localhost:8080/userapp/users/kingson").build();

        warmUp(httpClient, request);

        int repeatCount = 100;

        Reporter smeshReporter = new Reporter();
        execute("http://localhost:8080/userapp/users/kingson", repeatCount, httpClient, smeshReporter);

        Reporter directAccessReporter = new Reporter();
        execute("http://localhost:8081/users/kingson", repeatCount, httpClient, directAccessReporter);

        logger.info("================================ benchmark result ==============================");
        logger.info(smeshReporter.report());
        logger.info(directAccessReporter.report());
        logger.info("================================================================================");
    }

    private void execute(String url, int repeatCount, OkHttpClient httpClient, Reporter reporter) throws IOException {
        Request request = new Request.Builder().get().url(url).build();
        for (int i = 0; i < repeatCount; i++) {
            try {
                long startTime = System.nanoTime();
                Response response = httpClient.newCall(request).execute();
                if (response.code() == 200) {
                    reporter.report(System.nanoTime() - startTime);
                }
            } catch (Throwable ignored) {
            }
        }
    }

    private void warmUp(OkHttpClient httpClient, Request request) {
        boolean warmUp = Retry.exec(100, 3000, () -> {
            try {
                Response response = httpClient.newCall(request).execute();
                if (response.code() == 200) {
                    return true;
                }
            } catch (IOException ignored) {
            }
            return false;
        });
        logger.info("warm up result: {}", warmUp);
    }
}
