package com.github.lkq.smesh.consul.routes.v1;

import com.github.lkq.smesh.consul.client.ConsulClient;
import com.github.lkq.smesh.consul.ws.RegistrationWebSocket;
import com.github.lkq.smesh.server.Routes;
import spark.Service;

public class RegistrationRoutes implements Routes {

    private final RegistrationWebSocket handler;

    public RegistrationRoutes(ConsulClient client) {
        handler = new RegistrationWebSocket(client);
    }

    @Override
    public void ignite(Service service) {
        service.webSocket("/register", handler);
    }
}
