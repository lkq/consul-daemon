package com.github.lkq.smesh.consul.app;


import com.github.lkq.smesh.consul.config.Config;

import javax.inject.Inject;

public class App {

    private Config config;

    @Inject
    public App(Config config) {
        this.config = config;
    }

    public void start(int httpsPort) {
    }
}
