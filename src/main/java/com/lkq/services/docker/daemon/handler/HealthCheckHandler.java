package com.lkq.services.docker.daemon.handler;

import com.google.gson.Gson;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HealthCheckHandler {
    private static Logger logger = LoggerFactory.getLogger(HealthCheckHandler.class);
    private HttpClient httpClient;
    private String healthPath = "http://localhost:8500/v1/health/node/";
    private String nodeName;

    public HealthCheckHandler(String nodeName, HttpClient httpClient) {
        this.nodeName = nodeName;
        this.httpClient = httpClient;
    }

    public String handleHealthCheck(Request request, Response response) {
        String full = request.queryParams("full");
        try {
            ContentResponse statusResponse = httpClient.GET(healthPath + nodeName);
            String content = statusResponse.getContentAsString();
            if (full == null) {
                return getSimpleHealth(content);
            }
            logger.info("health check result: {}", content);
            return content;
        } catch (Exception e) {
            logger.error("failed to get node health", e);
        }
        return "false";
    }

    private String getSimpleHealth(String content) {
        List nodeHealth = new Gson().fromJson(content, ArrayList.class);
        if (nodeHealth.size() > 1) {
            logger.info("skipping node health {}", nodeHealth.subList(1, nodeHealth.size()));
        }
        if (nodeHealth.size() > 0) {
            Map health = (Map) nodeHealth.get(0);
            String status = (String) health.get("Status");
            return String.valueOf("passing".equals(status));
        }
        return "false";
    }
}
