package com.lkq.services.docker.daemon.handler;

import com.lkq.services.docker.daemon.consul.ConsulController;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class HealthCheckHandler {
    private static Logger logger = LoggerFactory.getLogger(HealthCheckHandler.class);
    private ConsulController consulController;
    private HttpClient httpClient;

    public HealthCheckHandler(ConsulController consulController, HttpClient httpClient) {

        this.consulController = consulController;
        this.httpClient = httpClient;
    }

    public String handleHealthCheck(Request request, Response response) {
        String full = request.queryParams("full");
        try {
            ContentResponse healthCheckResponse = httpClient.GET("http://localhost:8500/v1/health/service/consul?pretty");
            logger.info("health check result: {}", healthCheckResponse.getContentAsString());
            return healthCheckResponse.getContentAsString();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }
}
