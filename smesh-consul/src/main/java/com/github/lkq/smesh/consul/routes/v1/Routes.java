package com.github.lkq.smesh.consul.routes.v1;

import com.github.lkq.smesh.consul.health.ConsulHealthChecker;
import spark.Service;

public class Routes {

    private ConsulHealthChecker consulHealthChecker;

    public Routes(ConsulHealthChecker consulHealthChecker) {
        this.consulHealthChecker = consulHealthChecker;
    }

    public void ignite(Service service) {
        service.get("/smesh-consul/v1/health", consulHealthChecker::getNodeHealth);
    }
}
