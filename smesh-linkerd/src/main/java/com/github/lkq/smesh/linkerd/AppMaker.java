package com.github.lkq.smesh.linkerd;

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

    /**
     * put every piece together
     * @param network
     * @param portBindings
     * @param hostConfigPath
     * @param appVersion
     * @return
     */
    public App makeApp(String network, List<PortBinding> portBindings, String hostConfigPath, String appVersion) {
        String configFileName = Constants.LINKERD_CONFIG_PREFIX + "-" + appVersion + ".yaml";

        exportLinkerdConfig(hostConfigPath, configFileName);

        LinkerdContextFactory contextFactory = new LinkerdContextFactory();
        ContainerContext context = contextFactory.createDefaultContext()
                .volumeBindings(Arrays.asList(new VolumeBinding(hostConfigPath, Constants.CONTAINER_CONFIG_PATH)))
                .portBindings(portBindings)
                .network(network)
                .commandBuilder(new LinkerdCommandBuilder(Constants.CONTAINER_CONFIG_PATH + "/" + configFileName));

        LinkerdController linkerdController = new LinkerdController(SimpleDockerClient.create(DockerClientFactory.get()));

        WebServer webServer = new WebServer(8009, new LinkerdRoutes());

        return new App(context, linkerdController, webServer);
    }

    private void exportLinkerdConfig(String hostConfigPath, String configFileName) {
        ConfigExporter configExporter = new ConfigExporter();
        Map linkerdConfig = configExporter.loadFromResource(Constants.LINKERD_CONFIG_PREFIX + ".yaml");
        configExporter.writeToFile(linkerdConfig, new File(hostConfigPath, configFileName));
    }
}
