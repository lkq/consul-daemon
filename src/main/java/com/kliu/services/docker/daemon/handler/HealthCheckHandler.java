package com.kliu.services.docker.daemon.handler;

import com.kliu.services.docker.daemon.consul.ConsulDockerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

public class HealthCheckHandler {
    private static Logger logger = LoggerFactory.getLogger(HealthCheckHandler.class);
    private ConsulDockerController consulDockerController;

    public HealthCheckHandler(ConsulDockerController consulDockerController) {

        this.consulDockerController = consulDockerController;
    }

    public String handleHealthCheck(Request request, Response response) {
        String full = request.queryParams("full");
        boolean showFullDetails = full == null;
        return String.valueOf(true);
    }
}
