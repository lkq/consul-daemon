package com.github.lkq.smesh.server;

import spark.Service;

public class WebServer {
    private final int port;
    private Routes routes;
    private Service service;

    public WebServer(Routes routes, int port) {
        this.routes = routes;
        this.port = port;
    }

    public void start() {
        service = Service.ignite();
        service.port(port);
        routes.ignite(service);
    }

    public void stop() {
        if (service != null) {
            service.stop();
        }
    }
}
