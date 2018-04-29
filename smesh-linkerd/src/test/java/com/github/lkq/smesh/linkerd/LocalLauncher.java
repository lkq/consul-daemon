package com.github.lkq.smesh.linkerd;

import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.docker.DockerClientFactory;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import com.github.lkq.smesh.linkerd.api.LinkerdController;
import com.github.lkq.smesh.linkerd.context.LinkerdCommandBuilder;
import com.github.lkq.smesh.linkerd.context.LinkerdContextFactory;
import com.github.lkq.smesh.linkerd.routes.v1.LinkerdRoutes;
import com.github.lkq.smesh.logging.JulToSlf4jBridge;
import com.github.lkq.smesh.server.WebServer;
import org.slf4j.Logger;

public class LocalLauncher {
    private static Logger logger;
    public static void main(String[] args) {
        JulToSlf4jBridge.setup();
        new LocalLauncher().start();
    }

    private void start() {
        final LinkerdContextFactory contextFactory = new LinkerdContextFactory();
        ContainerContext context = contextFactory.createDefaultContext().commandBuilder(new LinkerdCommandBuilder());
        LinkerdController linkerdController = new LinkerdController(SimpleDockerClient.create(DockerClientFactory.get()));
        WebServer webServer = new WebServer(new LinkerdRoutes(), 8009);

        App app = new App(context, linkerdController, webServer);

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        app.start();
    }
}
