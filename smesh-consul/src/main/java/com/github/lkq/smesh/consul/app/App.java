package com.github.lkq.smesh.consul.app;


import com.github.lkq.instadocker.InstaDocker;
import com.github.lkq.smesh.consul.WebServer;
import com.github.lkq.smesh.consul.config.Config;
import com.github.lkq.smesh.consul.controller.ConsulController;
import com.github.lkq.smesh.consul.controller.VersionController;

import javax.inject.Inject;

public class App {

    private Config config;
    private ConsulController consulController;
    private VersionController versionController;
    private WebServer webServer;
    private InstaDocker container;

    @Inject
    public App(Config config, ConsulController consulController, VersionController versionController, WebServer webServer) {
        this.config = config;
        this.consulController = consulController;
        this.versionController = versionController;
        this.webServer = webServer;
    }

    public void start(int httpPort) {
        container = consulController.createContainer(config.consulContext());
        container.start(config.cleanStart(), 60);
        versionController.start();
        webServer.start(httpPort);
    }

    public InstaDocker instaDocker() {
        return container;
    }

    public void stop() {
        container.container().ensureStopped(60);
        webServer.stop();
    }
}
