package com.github.lkq.smesh.linkerd;

import com.github.lkq.smesh.AppVersion;
import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.context.VolumeBinder;
import com.github.lkq.smesh.docker.DockerClientFactory;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import com.github.lkq.smesh.linkerd.container.LinkerdController;
import com.github.lkq.smesh.linkerd.context.LinkerdCommandBuilder;
import com.github.lkq.smesh.linkerd.context.LinkerdContextFactory;
import com.github.lkq.smesh.linkerd.routes.v1.LinkerdRoutes;
import com.github.lkq.smesh.server.WebServer;

import java.util.Arrays;

public class AppMaker {

    public static final String CONTAINER_CONFIG_PATH = "/linkerd";

    public App makeApp() {
        String localConfigPath = AppMaker.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        LinkerdContextFactory contextFactory = new LinkerdContextFactory();
        ContainerContext context = contextFactory.createDefaultContext()
                .volumeBinders(Arrays.asList(new VolumeBinder(localConfigPath, CONTAINER_CONFIG_PATH)))
                .network("host")
                .commandBuilder(new LinkerdCommandBuilder(CONTAINER_CONFIG_PATH + "/smesh-linkerd-" + AppVersion.get(AppMaker.class)+ ".yaml"));

        LinkerdController linkerdController = new LinkerdController(SimpleDockerClient.create(DockerClientFactory.get()));

        WebServer webServer = new WebServer(new LinkerdRoutes(), 8009);

        return new App(context, linkerdController, webServer);
    }
}
