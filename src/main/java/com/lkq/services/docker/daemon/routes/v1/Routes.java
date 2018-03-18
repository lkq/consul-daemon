package com.lkq.services.docker.daemon.routes.v1;

import com.lkq.services.docker.daemon.health.ConsulHealthChecker;
import spark.Service;

public class Routes {

    private ConsulHealthChecker consulHealthChecker;

    public Routes(ConsulHealthChecker consulHealthChecker) {
        this.consulHealthChecker = consulHealthChecker;
    }

    public void ignite(Service service) {
        service.get("/consul-daemon/health", consulHealthChecker::getNodeHealth);
    }
}
