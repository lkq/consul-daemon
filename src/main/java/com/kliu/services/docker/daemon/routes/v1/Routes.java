package com.kliu.services.docker.daemon.routes.v1;

import com.kliu.services.docker.daemon.handler.HealthCheckHandler;
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
