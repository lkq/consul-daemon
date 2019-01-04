package com.github.lkq.smesh.test;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.lkq.smesh.context.PortBinding;
import com.github.lkq.smesh.docker.ContainerLogger;
import com.github.lkq.smesh.docker.ContainerNetwork;
import com.github.lkq.smesh.docker.DockerClientFactory;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import com.github.lkq.smesh.smesh4j.Service;
import com.github.lkq.smesh.smesh4j.Smesh;
import com.github.lkq.smesh.smesh4j.WebSocketClientFactory;
import com.github.lkq.smesh.test.app.UserAppImageBuilder;
import com.github.lkq.smesh.test.app.UserAppPackager;
import com.github.lkq.smesh.test.consul.ConsulMainLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import static com.github.lkq.smesh.linkerd.Constants.VAR_CONSUL_HOST;

public class TestEngine {
    public static final String USER_APP = "userapp";
    public static final int REG_PORT = 1025;
    public static final int LINKERD_PORT = 1026;

    private static Logger logger = LoggerFactory.getLogger(TestEngine.class);

    private SimpleDockerClient simpleDockerClient = SimpleDockerClient.create(DockerClientFactory.create());

    private UserAppPackager packager = new UserAppPackager();
    private UserAppImageBuilder imageBuilder = new UserAppImageBuilder(DockerClientFactory.create());

    private com.github.lkq.smesh.linkerd.App linkerdApp;

    private static Boolean started = false;

    private static TestEngine instance = new TestEngine();
    private String consulContainer;
    private String linkerdContainer;
    private String userAppContainer;

    public static TestEngine get() {
        return instance;
    }

    private TestEngine(){}

    public synchronized void startEverything() throws IOException, InterruptedException {
        if (!started) {
            consulContainer = startNewConsul(REG_PORT);
            linkerdContainer = startLinkerd(LINKERD_PORT, consulContainer);
            userAppContainer = startUserApp(8081, "ws://localhost:" + REG_PORT + "/smesh/register/v1");
            started = true;
        } else {
            logger.info("test engine already started");
        }
    }

    public void stopEverything() {
        stopConsul();
        linkerdApp.stop();
        simpleDockerClient.stopContainer(userAppContainer);
    }

    public String startNewConsul(int port) {
        return ConsulMainLocal.start(port);
    }

    public void stopConsul() {
        ConsulMainLocal.stop();
    }

    /**
     * start a local linkerd docker container with binding port 8080 (due to unable to use host network in mac)
     * @return container id
     * @param appPort
     */
    public String startLinkerd(int appPort, String consulContainer) {

        String hostConfigPath = ClassLoader.getSystemResource("").getPath();

        HashMap<String, String> configVariables = createLinkerdConfigVariables(consulContainer);

        com.github.lkq.smesh.linkerd.AppMaker appMaker = new com.github.lkq.smesh.linkerd.AppMaker();
        linkerdApp = appMaker.makeApp(appPort, ContainerNetwork.LINKERD_MAC, configVariables, hostConfigPath, "1.2.3");

        return linkerdApp.start();
    }

    /**
     * build a docker image containing UserApp and start it
     * @return container id
     * @param restPort
     * @param registerURL
     */
    public String startUserApp(int restPort, String registerURL) {
        String[] artifact = packager.buildPackage();
        return quickStartUserApp(restPort, registerURL, artifact[0], artifact[1]);
    }

    public String quickStartUserApp(int restPort, String registerURL, String artifactPath, String artifactName) {
        logger.info("test server build success: {}{}", artifactPath, artifactName);
        if (simpleDockerClient.imageExists(USER_APP)) {
            simpleDockerClient.removeImage(USER_APP);
        }
        String image = imageBuilder.build(artifactPath, artifactName, registerURL, USER_APP);

        if (simpleDockerClient.containerExists(USER_APP)) {
            simpleDockerClient.stopContainer(USER_APP);
            simpleDockerClient.removeContainer(USER_APP);
        }
        String containerId = simpleDockerClient.createContainer(image, USER_APP)
                .withPortBinders(Arrays.asList(new PortBinding(restPort, PortBinding.Protocol.TCP)))
                .build();
        simpleDockerClient.startContainer(containerId);
        simpleDockerClient.attachLogging(containerId, new ContainerLogger());

        registerUserApp(containerId, restPort, registerURL);

        return containerId;
    }

    private void registerUserApp(String containerId, int restPort, String registerURL) {
        try {
            InspectContainerResponse container = simpleDockerClient.inspectContainer(containerId);
            String address = container.getNetworkSettings().getNetworks().get("bridge").getIpAddress();
            String service = Service.create()
                    .withID("userapp-" + System.currentTimeMillis())
                    .withName("userapp")
                    .withAddress(address)
                    .withPort(restPort).build();

            Smesh.register(new String[]{registerURL}, service, new WebSocketClientFactory(), 10000);
        } catch (Exception e) {
            logger.error("failed to register user app", e);
        }
    }

    private HashMap<String, String> createLinkerdConfigVariables(String consulContainer) {
        SimpleDockerClient docker = SimpleDockerClient.create();
        InspectContainerResponse consul = docker.inspectContainer(consulContainer);
        String consulIP = consul.getNetworkSettings().getNetworks().get("bridge").getIpAddress();
        HashMap<String, String> configVariables = new HashMap<>();
        configVariables.put(VAR_CONSUL_HOST, consulIP);
        return configVariables;
    }
}
