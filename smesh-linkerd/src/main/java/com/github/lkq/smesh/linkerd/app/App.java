package com.github.lkq.smesh.linkerd.app;


import com.github.lkq.instadocker.InstaDocker;
import com.github.lkq.smesh.linkerd.WebServer;
import com.github.lkq.smesh.linkerd.config.Config;
import com.github.lkq.smesh.linkerd.config.ConfigProcessor;
import com.github.lkq.smesh.linkerd.config.LinkerdContext;
import com.github.lkq.smesh.linkerd.container.LinkerdController;
import com.google.common.collect.ImmutableMap;

import javax.inject.Inject;

import static com.github.lkq.smesh.linkerd.Constants.VAR_CONSUL_HOST;

public class App {

    private Config config;
    private LinkerdController linkerdController;
    private WebServer webServer;
    private InstaDocker container;
    private ConfigProcessor configProcessor;

    @Inject
    public App(Config config, LinkerdController linkerdController, WebServer webServer, ConfigProcessor configProcessor) {
        this.config = config;
        this.linkerdController = linkerdController;
        this.webServer = webServer;
        this.configProcessor = configProcessor;
    }

    public void start(int httpPort) {
        LinkerdContext context = config.linkerdContext();
        configProcessor.process("/template", "smesh-linkerd.yaml", String.join("/", context.hostConfigFilePath(), context.configFileName()), context.templateVariables());
        container = linkerdController.createContainer(context);
        container.start(config.cleanStart(), 60);
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
