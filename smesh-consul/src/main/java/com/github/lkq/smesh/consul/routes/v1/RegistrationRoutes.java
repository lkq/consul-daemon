package com.github.lkq.smesh.consul.routes.v1;

import com.github.lkq.smesh.consul.client.ConsulClient;
import com.github.lkq.smesh.consul.register.ConsulRegistrar;
import com.github.lkq.smesh.consul.register.RegistrationWebSocket;
import com.github.lkq.smesh.server.Routes;
import spark.Service;

public class RegistrationRoutes implements Routes {

    private final RegistrationWebSocket handler;

    public RegistrationRoutes(ConsulClient client) {
        handler = new RegistrationWebSocket(client, () -> new ConsulRegistrar(client));
    }

    @Override
    public void ignite(Service service) {
        service.webSocket("/register/v1", handler);
    }
}
