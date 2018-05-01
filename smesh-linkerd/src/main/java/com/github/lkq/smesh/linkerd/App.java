package com.github.lkq.smesh.linkerd;

import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.linkerd.container.LinkerdController;
import com.github.lkq.smesh.server.WebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static Logger logger = LoggerFactory.getLogger(App.class);
    private final LinkerdController linkerdController;
    private ContainerContext context;
    private final WebServer webServer;

    /**
     * application entry point, a place to put together different pieces and make it run
     *
     * @param context
     * @param linkerdController
     * @param webServer
     */
    public App(ContainerContext context, LinkerdController linkerdController, WebServer webServer) {
        this.linkerdController = linkerdController;
        this.context = context;
        this.webServer = webServer;
    }

    /**
     * start the application
     *
     */
    public void start() {
        linkerdController.stopAndRemoveExistingInstance(context.nodeName());
        linkerdController.startNewInstance(context);
        linkerdController.attachLogging(context.nodeName());
        webServer.start();
    }

    public void stop() {

    }

}
