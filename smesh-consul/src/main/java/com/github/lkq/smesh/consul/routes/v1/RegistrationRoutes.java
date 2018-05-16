package com.github.lkq.smesh.consul.routes.v1;

import com.github.lkq.smesh.consul.ws.RegistrationWebSocket;
import com.github.lkq.smesh.server.Routes;
import spark.Service;

public class RegistrationRoutes implements Routes {
    @Override
    public void ignite(Service service) {
        service.webSocket("/register", RegistrationWebSocket.class);
    }
}
