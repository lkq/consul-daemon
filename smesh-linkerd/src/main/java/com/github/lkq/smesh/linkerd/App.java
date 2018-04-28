package com.github.lkq.smesh.linkerd;

import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.docker.DockerClientFactory;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import com.github.lkq.smesh.linkerd.api.LinkerdController;
import com.github.lkq.smesh.linkerd.context.LinkerdCommandBuilder;
import com.github.lkq.smesh.linkerd.context.LinkerdContextFactory;
import com.github.lkq.smesh.linkerd.routes.v1.LinkerdRoutes;
import com.github.lkq.smesh.server.WebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static Logger logger = LoggerFactory.getLogger(App.class);

    /**
     * application entry point, a place to put together different pieces and make it run
     *
     */
    public App() {

    }

    /**
     * start the application
     *
     */
    public void start() {
        LinkerdController controller = new LinkerdController(SimpleDockerClient.create(DockerClientFactory.get()));
        LinkerdContextFactory contextFactory = new LinkerdContextFactory();
        ContainerContext context = contextFactory.createDefaultContext().commandBuilder(new LinkerdCommandBuilder());
        controller.stopAndRemoveExistingInstance(context.nodeName());
        controller.startNewInstance(context);
        controller.attachLogging(context.nodeName());
        WebServer webServer = new WebServer(new LinkerdRoutes(), 8009);
        webServer.start();
    }

    public void stop() {

    }

}
