package com.github.lkq.smesh.linkerd;

import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.context.VolumeBinding;
import com.github.lkq.smesh.docker.ContainerNetwork;
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

import static com.github.lkq.smesh.linkerd.Constants.*;

public class AppMaker {

    private static Logger logger = LoggerFactory.getLogger(AppMaker.class);

    /**
     * put every piece together
     *
     * @param network
     * @param configVariables
     * @param hostConfigPath
     * @param appVersion
     * @return
     */
    public App makeApp(int restPort, ContainerNetwork network, HashMap<String, String> configVariables, String hostConfigPath, String appVersion) {

        String configFileName = LINKERD_CONFIG_FILE_PREFIX + "-" + appVersion + ".yaml";

        processConfig(new ConfigProcessor(), configVariables, "/template", "smesh-linkerd.yaml", hostConfigPath, configFileName);

        LinkerdContextFactory contextFactory = new LinkerdContextFactory();
        ContainerContext context = contextFactory.createDefaultContext()
                .volumeBindings(new VolumeBinding(hostConfigPath, LINKERD_CONFIG_FILE_PATH))
                .portBindings(network.portBindings())
                .network(network.network())
                .commandBuilder(new LinkerdCommandBuilder(LINKERD_CONFIG_FILE_PATH + "/" + configFileName));

        LinkerdController linkerdController = new LinkerdController(logger, SimpleDockerClient.create());
        WebServer webServer = new WebServer(restPort, new LinkerdRoutes());

        return new App(context, linkerdController, webServer);
    }

    private void processConfig(ConfigProcessor processor, HashMap<String, String> variables, String templateRoot, String sourceFileName, String hostConfigRoot, String targetFileName) {

        String configContent = processor.load(templateRoot, sourceFileName, variables, AppMaker.class);

        try {
            File targetFile = new File(hostConfigRoot, targetFileName);
            FileUtils.writeStringToFile(targetFile,
                    configContent,
                    com.github.lkq.smesh.Constants.ENCODING_UTF8);
            logger.info("writing linkerd config to: {}", targetFile);
        } catch (IOException e) {
            throw new SmeshException("failed to write config to file: " + hostConfigRoot + "/" + targetFileName, e);
        }
    }

}
