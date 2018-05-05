package com.github.lkq.smesh.linkerd;

import com.github.lkq.smesh.AppVersion;
import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.context.PortBinding;
import com.github.lkq.smesh.context.VolumeBinding;
import com.github.lkq.smesh.docker.DockerClientFactory;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import com.github.lkq.smesh.linkerd.config.ConfigExporter;
import com.github.lkq.smesh.linkerd.container.LinkerdController;
import com.github.lkq.smesh.linkerd.context.LinkerdCommandBuilder;
import com.github.lkq.smesh.linkerd.context.LinkerdContextFactory;
import com.github.lkq.smesh.linkerd.routes.v1.LinkerdRoutes;
import com.github.lkq.smesh.server.WebServer;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AppMaker {

    public static final String CONTAINER_CONFIG_PATH = "/linkerd";

    public App makeApp(String network, List<PortBinding> portBindings, String localConfigPath) {
        String configFileName = "smesh-linkerd-" + AppVersion.get(AppMaker.class) + ".yaml";

        ConfigExporter configExporter = new ConfigExporter();
        Map linkerdConfig = configExporter.loadFromResource("smesh-linkerd.yaml");
        configExporter.writeToFile(linkerdConfig, new File(localConfigPath, configFileName));

        LinkerdContextFactory contextFactory = new LinkerdContextFactory();
        ContainerContext context = contextFactory.createDefaultContext()
                .volumeBinders(Arrays.asList(new VolumeBinding(localConfigPath, CONTAINER_CONFIG_PATH)))
                .portBindings(portBindings)
                .network(network)
                .commandBuilder(new LinkerdCommandBuilder(CONTAINER_CONFIG_PATH + "/" + configFileName));

        LinkerdController linkerdController = new LinkerdController(SimpleDockerClient.create(DockerClientFactory.get()));

        WebServer webServer = new WebServer(new LinkerdRoutes(), 8009);

        return new App(context, linkerdController, webServer);
    }
}
