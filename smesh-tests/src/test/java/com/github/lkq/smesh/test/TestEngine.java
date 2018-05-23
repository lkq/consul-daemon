package com.github.lkq.smesh.test;

import com.github.dockerjava.api.DockerClient;
import com.github.lkq.smesh.consul.App;
import com.github.lkq.smesh.consul.AppMaker;
import com.github.lkq.smesh.consul.client.ConsulClient;
import com.github.lkq.smesh.consul.client.ResponseParser;
import com.github.lkq.smesh.consul.client.http.SimpleHttpClient;
import com.github.lkq.smesh.consul.command.ConsulCommandBuilder;
import com.github.lkq.smesh.context.PortBinding;
import com.github.lkq.smesh.docker.ContainerLogger;
import com.github.lkq.smesh.docker.ContainerNetwork;
import com.github.lkq.smesh.docker.DockerClientFactory;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import com.github.lkq.smesh.smesh4j.Service;
import com.github.lkq.smesh.smesh4j.Smesh;
import com.github.lkq.smesh.test.app.UserAppImageBuilder;
import com.github.lkq.smesh.test.app.UserAppPackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

public class TestEngine {

    public static final String USER_APP_CONTAINER_ID = "userapp";
    private static Logger logger = LoggerFactory.getLogger(TestEngine.class);

    private final UserAppPackager packager = new UserAppPackager();
    private final DockerClient dockerClient = DockerClientFactory.get();
    private final SimpleDockerClient simpleDockerClient = SimpleDockerClient.create(dockerClient);
    private final UserAppImageBuilder imageBuilder = new UserAppImageBuilder(dockerClient);

    public void startEverything() throws IOException, InterruptedException {

        String consul = startConsul(1025);
        String linkerd = startLinkerd(1026);
        String userApp = startUserApp(8081, "ws://172.17.0.2:1025/register");
    }

    /**
     * start a local consul docker container with binding port 8500 (due to unable to use host network in mac)
     * @return container id
     * @param appPort
     */
    public String startConsul(int appPort) {
        AppMaker appMaker = new AppMaker();

        ConsulCommandBuilder serverCommand = ConsulCommandBuilder.server(true, Collections.emptyList())
                .ui(true)
                .clientIP("0.0.0.0")
                .bootstrap(true);

        String nodeName = "consul";
        String localDataPath = ClassLoader.getSystemResource(".").getPath() + "data/" + nodeName + "-" + System.currentTimeMillis();
        if (new File(localDataPath).mkdirs()) {
            logger.info("created config dir: {}", localDataPath);
        }
        ConsulClient consulClient = new ConsulClient(new SimpleHttpClient(), new ResponseParser(), "http://localhost:8500");
        App app = appMaker.makeApp(
                appPort,
                nodeName,
                ContainerNetwork.CONSUL_MAC,
                serverCommand,
                localDataPath,
                "1.2.3",
                consulClient);

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        return app.start(true);
    }

    /**
     * start a local linkerd docker container with binding port 8080 (due to unable to use host network in mac)
     * @return container id
     * @param appPort
     */
    public String startLinkerd(int appPort) {

        String localConfigPath = com.github.lkq.smesh.linkerd.AppMaker.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        com.github.lkq.smesh.linkerd.AppMaker appMaker = new com.github.lkq.smesh.linkerd.AppMaker();
        com.github.lkq.smesh.linkerd.App app = appMaker.makeApp(appPort, ContainerNetwork.LINKERD_MAC, localConfigPath, "1.2.3");

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        return app.start();
    }

    /**
     * build a docker image containing UserApp and start it
     * @return container id
     * @param restPort
     * @param registerURL
     */
    public String startUserApp(int restPort, String registerURL) {
        String[] artifact = packager.buildPackage();
        logger.info("test server build success: {}/{}", artifact[0], artifact[1]);
        String image = imageBuilder.build(artifact[0], artifact[1], registerURL);

        if (simpleDockerClient.containerExists(USER_APP_CONTAINER_ID)) {
            simpleDockerClient.removeContainer(USER_APP_CONTAINER_ID);
        }
        String containerId = simpleDockerClient.createContainer(image, USER_APP_CONTAINER_ID)
                .withPortBinders(Arrays.asList(new PortBinding(restPort, PortBinding.Protocol.TCP)))
                .build();
        simpleDockerClient.startContainer(containerId);
        simpleDockerClient.attachLogging(containerId, new ContainerLogger());

        registerUserApp();

        return containerId;
    }

    private void registerUserApp() {
        try {
            String service = Service.create()
                    .withID("userapp-" + System.currentTimeMillis())
                    .withName("userapp")
                    .withAddress("172.17.0.3")
                    .withPort(8081).build();

            Smesh smesh = new Smesh(new URI("ws://localhost:1025/register"));
            smesh.register(service);
            System.out.println("service registered: " + service);
        } catch (Exception e) {
            logger.error("failed to register user app", e);
        }
    }

}
