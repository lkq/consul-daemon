package com.github.lkq.smesh.consul.app;


import com.github.lkq.instadocker.InstaDocker;
import com.github.lkq.smesh.consul.config.Config;
import com.github.lkq.smesh.consul.container.ConsulController;
import com.github.lkq.smesh.consul.controller.VersionController;

import javax.inject.Inject;

public class App {

    private Config config;
    private ConsulController consulController;
    private VersionController versionController;

    @Inject
    public App(Config config, ConsulController consulController, VersionController versionController) {
        this.config = config;
        this.consulController = consulController;
        this.versionController = versionController;
    }

    public void start(int httpPort) {
        InstaDocker container = consulController.createContainer(config.consulContext());
        container.start(config.cleanStart(), 60);
        versionController.start();
    }
}
