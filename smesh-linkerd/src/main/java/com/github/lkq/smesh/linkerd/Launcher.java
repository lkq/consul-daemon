package com.github.lkq.smesh.linkerd;

import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.docker.DockerClientFactory;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import com.github.lkq.smesh.exception.SmeshException;
import com.github.lkq.smesh.linkerd.api.LinkerdController;
import com.github.lkq.smesh.linkerd.context.LinkerdCommandBuilder;
import com.github.lkq.smesh.linkerd.context.LinkerdContextFactory;
import com.github.lkq.smesh.linkerd.routes.v1.LinkerdRoutes;
import com.github.lkq.smesh.logging.JulToSlf4jBridge;
import com.github.lkq.smesh.server.WebServer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;

public class Launcher {
    public static void main(String[] args) {
        JulToSlf4jBridge.setup();
        new Launcher().start();
    }

    private void start() {
        final LinkerdContextFactory contextFactory = new LinkerdContextFactory();
        LinkerdCommandBuilder commandBuilder = new LinkerdCommandBuilder()
                .config(loadConfig());
        ContainerContext context = contextFactory.createDefaultContext()
                .commandBuilder(commandBuilder);
        LinkerdController linkerdController = new LinkerdController(SimpleDockerClient.create(DockerClientFactory.get()));
        WebServer webServer = new WebServer(new LinkerdRoutes(), 8009);
        App app = new App(context, linkerdController, webServer);

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        app.start();
    }

    private String loadConfig() {
        URL fileURL = ClassLoader.getSystemResource("config.yaml");
        try {
            if (fileURL != null) {
                return FileUtils.readFileToString(new File(fileURL.getFile()), "UTF-8");
            }
        } catch (Exception e) {
            throw new SmeshException("failed to load linkerd config:" + fileURL, e);
        }
        throw new SmeshException("failed to load linkerd config:" + fileURL);
    }

}
