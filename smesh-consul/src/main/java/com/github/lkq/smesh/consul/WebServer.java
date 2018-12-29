package com.github.lkq.smesh.consul;

import com.github.lkq.smesh.consul.handler.ProfileHandler;
import com.github.lkq.smesh.consul.handler.v1.RegisterWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WebServer {
    public static final String SMESH = "/smesh";
    private static Logger logger = LoggerFactory.getLogger(WebServer.class);

    private Service service;
    private ProfileHandler profileHandler;
    private RegisterWebSocketHandler registerWebSocketHandler;

    @Inject
    public WebServer(ProfileHandler profileHandler, RegisterWebSocketHandler registerWebSocketHandler) {
        this.profileHandler = profileHandler;
        this.registerWebSocketHandler = registerWebSocketHandler;
    }

    public void start(int port) {
        service = Service.ignite();
        service.port(port);

        service.webSocket(SMESH + "/register/v1", registerWebSocketHandler);

        service.get(SMESH + "/profile", profileHandler::getProfile);

    }

    public void stop() {
        if (service != null) {
            service.stop();
        }
    }
}
