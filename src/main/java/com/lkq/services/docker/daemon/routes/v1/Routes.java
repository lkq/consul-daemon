package com.lkq.services.docker.daemon.routes.v1;

import com.lkq.services.docker.daemon.handler.HealthCheckHandler;
import spark.Spark;

public class Routes {

    private HealthCheckHandler healthCheckHandler;

    public Routes(HealthCheckHandler healthCheckHandler) {
        this.healthCheckHandler = healthCheckHandler;
    }

    public void ignite() {
        Spark.get("/consul-daemon/health", healthCheckHandler::handleHealthCheck);
    }
}
