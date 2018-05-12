package com.github.lkq.smesh.linkerd;

import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.linkerd.container.LinkerdController;
import com.github.lkq.smesh.server.WebServer;

public class App {
    private final LinkerdController linkerdController;
    private ContainerContext context;
    private final WebServer webServer;

    public App(ContainerContext context, LinkerdController linkerdController, WebServer webServer) {
        this.linkerdController = linkerdController;
        this.context = context;
        this.webServer = webServer;
    }

    public String start() {
        linkerdController.stopAndRemoveExistingInstance(context.nodeName());
        linkerdController.startNewInstance(context);
        linkerdController.attachLogging(context.nodeName());
        webServer.start();
        return context.nodeName();
    }

    public void stop() {
        linkerdController.stop(context.nodeName());
    }

}
