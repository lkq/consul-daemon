package com.github.lkq.smesh.linkerd.app;


import com.github.lkq.instadocker.InstaDocker;
import com.github.lkq.smesh.linkerd.WebServer;
import com.github.lkq.smesh.linkerd.config.Config;
import com.github.lkq.smesh.linkerd.config.TemplateProcessor;
import com.github.lkq.smesh.linkerd.config.LinkerdContext;
import com.github.lkq.smesh.linkerd.controller.LinkerdController;

import javax.inject.Inject;

public class App {

    private Config config;
    private LinkerdController linkerdController;
    private WebServer webServer;
    private InstaDocker container;
    private TemplateProcessor templateProcessor;

    @Inject
    public App(Config config, LinkerdController linkerdController, WebServer webServer, TemplateProcessor templateProcessor) {
        this.config = config;
        this.linkerdController = linkerdController;
        this.webServer = webServer;
        this.templateProcessor = templateProcessor;
    }

    public void start(int httpPort) {
        LinkerdContext context = config.linkerdContext();
        templateProcessor.process("/template", "smesh-linkerd.yaml", String.join("/", context.hostConfigFilePath(), context.configFileName()), context.templateVariables());
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
