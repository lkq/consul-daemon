package com.github.lkq.smesh.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;

public class WebServer {
    private static Logger logger = LoggerFactory.getLogger(WebServer.class);

    private final int port;
    private Routes[] routes;
    private Service service;

    public WebServer(int port, Routes... routes) {
        this.routes = routes;
        this.port = port;
    }

    public void start() {
        service = Service.ignite();
        service.port(port);
        for (Routes route : routes) {
            logger.info("enabling routes: {}", route.getClass().getName());
            route.ignite(service);
        }
    }

    public void stop() {
        if (service != null) {
            service.stop();
        }
    }
}
