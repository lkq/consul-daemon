package com.github.lkq.smesh.consul.routes.v1;

import com.github.lkq.smesh.consul.health.ConsulHealthChecker;
import com.github.lkq.smesh.server.Routes;
import spark.Service;

public class ConsulRoutes implements Routes {

    private ConsulHealthChecker consulHealthChecker;

    public ConsulRoutes(ConsulHealthChecker consulHealthChecker) {
        this.consulHealthChecker = consulHealthChecker;
    }

    @Override
    public void ignite(Service service) {
        service.get("/smesh-consul/v1/health", consulHealthChecker::getNodeHealth);
    }
}
