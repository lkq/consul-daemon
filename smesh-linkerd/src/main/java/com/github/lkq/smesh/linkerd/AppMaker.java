package com.github.lkq.smesh.linkerd;

import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.context.VolumeBinding;
import com.github.lkq.smesh.docker.ContainerNetwork;
import com.github.lkq.smesh.docker.DockerClientFactory;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import com.github.lkq.smesh.linkerd.config.ConfigExporter;
import com.github.lkq.smesh.linkerd.container.LinkerdController;
import com.github.lkq.smesh.linkerd.context.LinkerdCommandBuilder;
import com.github.lkq.smesh.linkerd.context.LinkerdContextFactory;
import com.github.lkq.smesh.linkerd.routes.v1.LinkerdRoutes;
import com.github.lkq.smesh.server.WebServer;

import java.io.File;
import java.util.Map;

public class AppMaker {

    /**
     * put every piece together
     * @param network
     * @param hostConfigPath
     * @param appVersion
     * @return
     */
    public App makeApp(int restPort, ContainerNetwork network, String hostConfigPath, String appVersion) {
        String containerConfigFileName = Constants.LINKERD_CONFIG_PREFIX + "-" + appVersion + ".yaml";

        exportLinkerdConfig(hostConfigPath, containerConfigFileName);

        LinkerdContextFactory contextFactory = new LinkerdContextFactory();
        ContainerContext context = contextFactory.createDefaultContext()
                .volumeBindings(new VolumeBinding(hostConfigPath, Constants.CONTAINER_CONFIG_PATH))
                .portBindings(network.portBindings())
                .network(network.network())
                .commandBuilder(new LinkerdCommandBuilder(Constants.CONTAINER_CONFIG_PATH + "/" + containerConfigFileName));

        LinkerdController linkerdController = new LinkerdController(SimpleDockerClient.create(DockerClientFactory.get()));
        WebServer webServer = new WebServer(restPort, new LinkerdRoutes());

        return new App(context, linkerdController, webServer);
    }

    private void exportLinkerdConfig(String hostConfigPath, String configFileName) {
        ConfigExporter configExporter = new ConfigExporter();
        Map config = configExporter.loadFromResource("smesh-linkerd.yaml");
        configExporter.writeToFile(new File(hostConfigPath, configFileName), config);
    }
}
