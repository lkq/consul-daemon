package com.github.lkq.smesh.test;

import com.github.dockerjava.api.DockerClient;
import com.github.lkq.smesh.consul.App;
import com.github.lkq.smesh.consul.AppMaker;
import com.github.lkq.smesh.consul.command.ConsulCommandBuilder;
import com.github.lkq.smesh.consul.env.Environment;
import com.github.lkq.smesh.context.PortBinding;
import com.github.lkq.smesh.docker.DockerClientFactory;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import com.github.lkq.smesh.test.app.UserAppImageBuilder;
import com.github.lkq.smesh.test.app.UserAppPackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestEngine {

    public static final String USER_APP_CONTAINER_ID = "user-app";
    private static Logger logger = LoggerFactory.getLogger(TestEngine.class);

    private final UserAppPackager packager = new UserAppPackager();
    private final DockerClient dockerClient = DockerClientFactory.get();
    private final SimpleDockerClient simpleDockerClient = SimpleDockerClient.create(dockerClient);
    private final UserAppImageBuilder imageBuilder = new UserAppImageBuilder(dockerClient);

    public void startEverything() throws IOException, InterruptedException {

//        String consul = startConsul();
//        String linkerd = startLinkerd();
        String userApp = startUserApp();
    }

    /**
     * start a local consul docker container with binding port 8500 (due to unable to use host network in mac)
     * @return container id
     */
    private String startConsul() {
        AppMaker appMaker = new AppMaker();

        ConsulCommandBuilder serverCommand = ConsulCommandBuilder.server(true, Collections.emptyList())
                .ui(true)
                .clientIP("0.0.0.0")
                .bootstrap(true);

        String nodeName = "consul";
        String localDataPath = ClassLoader.getSystemResource(".").getPath() + "data/" + nodeName + "-" + System.currentTimeMillis();
        App app = appMaker.makeApp(nodeName, serverCommand, "", Arrays.asList(new PortBinding(8500, PortBinding.Protocol.TCP)), "1.2.3", 1025, localDataPath);

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        return app.start(Environment.get().forceRestart());
    }

    /**
     * start a local linkerd docker container with binding port 8080 (due to unable to use host network in mac)
     * @return container id
     */
    private String startLinkerd() {

        List<PortBinding> portBindings = Arrays.asList(new PortBinding(9990, PortBinding.Protocol.TCP),
                new PortBinding(8080, PortBinding.Protocol.TCP));
        String localConfigPath = com.github.lkq.smesh.linkerd.AppMaker.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        com.github.lkq.smesh.linkerd.AppMaker appMaker = new com.github.lkq.smesh.linkerd.AppMaker();
        com.github.lkq.smesh.linkerd.App app = appMaker.makeApp("", portBindings, localConfigPath, "1.2.3");

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        return app.start();
    }

    /**
     * build a docker image containing UserApp and start it
     * @return container id
     */
    private String startUserApp() {
        String[] artifact = packager.buildPackage();
        logger.info("test server build success: {}/{}", artifact[0], artifact[1]);
        String image = imageBuilder.build(artifact[0], artifact[1]);

        if (simpleDockerClient.containerExists(USER_APP_CONTAINER_ID)) {
            simpleDockerClient.removeContainer(USER_APP_CONTAINER_ID);
        }
        String containerId = simpleDockerClient.createContainer(image, USER_APP_CONTAINER_ID)
                .withPortBinders(Arrays.asList(new PortBinding(8081, PortBinding.Protocol.TCP)))
                .build();
        simpleDockerClient.startContainer(containerId);
        return containerId;
    }

}
