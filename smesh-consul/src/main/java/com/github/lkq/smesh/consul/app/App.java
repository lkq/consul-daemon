package com.github.lkq.smesh.consul.app;


import com.github.lkq.instadocker.InstaDocker;
import com.github.lkq.smesh.consul.config.Config;
import com.github.lkq.smesh.consul.container.ConsulController;

import javax.inject.Inject;

public class App {

    private Config config;
    private ConsulController consulController;

    @Inject
    public App(Config config, ConsulController consulController) {
        this.config = config;
        this.consulController = consulController;
    }

    public void start(int httpPort) {
        InstaDocker container = consulController.createContainer(config.consulContext());
        container.start(60);
    }
}
