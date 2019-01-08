package com.github.lkq.smesh.linkerd;

import com.github.lkq.smesh.linkerd.handler.ProfileHandler;
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

    @Inject
    public WebServer(ProfileHandler profileHandler) {
        this.profileHandler = profileHandler;
    }

    public void start(int port) {
        service = Service.ignite();
        service.port(port);

        service.get(SMESH + "/profile", profileHandler::getProfile);

    }

    public void stop() {
        if (service != null) {
            service.stop();
        }
    }
}
