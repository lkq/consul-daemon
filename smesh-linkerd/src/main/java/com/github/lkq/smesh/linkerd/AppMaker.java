package com.github.lkq.smesh.linkerd;

import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.context.VolumeBinding;
import com.github.lkq.smesh.docker.ContainerNetwork;
import com.github.lkq.smesh.docker.DockerClientFactory;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import com.github.lkq.smesh.exception.SmeshException;
import com.github.lkq.smesh.linkerd.config.ConfigProcessor;
import com.github.lkq.smesh.linkerd.container.LinkerdController;
import com.github.lkq.smesh.linkerd.context.LinkerdCommandBuilder;
import com.github.lkq.smesh.linkerd.context.LinkerdContextFactory;
import com.github.lkq.smesh.linkerd.routes.v1.LinkerdRoutes;
import com.github.lkq.smesh.server.WebServer;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class AppMaker {

    private static Logger logger = LoggerFactory.getLogger(AppMaker.class);

    /**
     * put every piece together
     *
     * @param network
     * @param hostConfigPath
     * @param appVersion
     * @return
     */
    public App makeApp(int restPort, ContainerNetwork network, String hostConfigPath, String appVersion) {

        String configFileName = Constants.LINKERD_CONFIG_PREFIX + "-" + appVersion + ".yaml";

        HashMap<String, String> variables = new HashMap<>();
        variables.put(com.github.lkq.smesh.linkerd.Constants.VAR_CONSUL_HOST, "172.17.0.2");
        processConfig(new ConfigProcessor(), variables, "/template", Constants.CONFIG_FINENAME, hostConfigPath, configFileName);

        LinkerdContextFactory contextFactory = new LinkerdContextFactory();
        ContainerContext context = contextFactory.createDefaultContext()
                .volumeBindings(new VolumeBinding(hostConfigPath, Constants.CONTAINER_CONFIG_PATH))
                .portBindings(network.portBindings())
                .network(network.network())
                .commandBuilder(new LinkerdCommandBuilder(Constants.CONTAINER_CONFIG_PATH + "/" + configFileName));

        LinkerdController linkerdController = new LinkerdController(SimpleDockerClient.create(DockerClientFactory.get()));
        WebServer webServer = new WebServer(restPort, new LinkerdRoutes());

        return new App(context, linkerdController, webServer);
    }

    private void processConfig(ConfigProcessor processor, HashMap<String, String> variables, String templateRoot, String sourceFileName, String hostConfigRoot, String targetFileName) {

        String configContent = processor.process(templateRoot, sourceFileName, variables, AppMaker.class);

        try {
            FileUtils.writeStringToFile(new File(hostConfigRoot, targetFileName),
                    configContent,
                    com.github.lkq.smesh.Constants.ENCODING_UTF8);
        } catch (IOException e) {
            throw new SmeshException("failed to write config to file: " + hostConfigRoot + "/" + targetFileName, e);
        }
    }

}
