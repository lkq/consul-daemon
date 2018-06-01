package com.github.lkq.smesh.consul.routes.v1;

import com.github.lkq.smesh.consul.client.ServiceRegistrar;
import com.github.lkq.smesh.consul.register.RegistrationWebSocket;
import com.github.lkq.smesh.consul.register.ResponseFactory;
import com.github.lkq.smesh.server.Routes;
import spark.Service;

public class RegistrationRoutes implements Routes {

    private final RegistrationWebSocket handler;

    public RegistrationRoutes(ServiceRegistrar registrar) {
        handler = new RegistrationWebSocket(registrar, new ResponseFactory());
    }

    @Override
    public void ignite(Service service) {
        service.webSocket("/register/v1", handler);
    }
}
