package com.github.lkq.smesh.test;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.lkq.instadocker.InstaDocker;
import com.github.lkq.instadocker.docker.DockerContainer;
import com.github.lkq.smesh.docker.DockerClientFactory;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import com.github.lkq.smesh.exception.SmeshException;
import com.github.lkq.smesh.smesh4j.Service;
import com.github.lkq.smesh.smesh4j.Smesh;
import com.github.lkq.smesh.smesh4j.WebSocketClientFactory;
import com.github.lkq.smesh.test.app.UserAppImageBuilder;
import com.github.lkq.smesh.test.app.UserAppPackager;
import com.github.lkq.smesh.test.consul.ConsulMainLocal;
import com.github.lkq.smesh.test.linkerd.LinkerdMainLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

public class TestEngine {
    public static final String USER_APP = "userapp";
    public static final int REG_PORT = 1025;
    public static final int LINKERD_PORT = 1026;

    private static Logger logger = LoggerFactory.getLogger(TestEngine.class);

    private SimpleDockerClient simpleDockerClient = SimpleDockerClient.create(DockerClientFactory.create());

    private UserAppPackager packager = new UserAppPackager();
    private UserAppImageBuilder imageBuilder = new UserAppImageBuilder(DockerClientFactory.create());

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
            consulContainer = startConsul(REG_PORT);
            linkerdContainer = startLinerd(LINKERD_PORT, consulContainer);
            userAppContainer = startUserApp(8081, "ws://localhost:" + REG_PORT + "/smesh/register/v1");
            started = true;
        } else {
            logger.info("test engine already started");
        }
    }

    public void stopEverything() {
        stopConsul();
        stopLinkerd();
        simpleDockerClient.stopContainer(userAppContainer);
    }

    /**
     * start a local consul docker container
     * @return container id
     * @param port
     */
    public String startConsul(int port) {
        return ConsulMainLocal.start(port);
    }

    public void stopConsul() {
        ConsulMainLocal.stop();
    }

    /**
     * start a local linkerd docker container with binding port 8080 (due to unable to use host network in mac)
     * @return container id
     * @param port
     */
    public String startLinerd(int port, String consulContainer) {
        return LinkerdMainLocal.start(port, consulContainer);
    }

    private void stopLinkerd() {
        LinkerdMainLocal.stop();
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
        logger.info("installing test app: {}{}", artifactPath, artifactName);
        String image = imageBuilder.build(artifactPath, artifactName, registerURL, USER_APP);
        InstaDocker userApp = new InstaDocker(image, USER_APP)
                .dockerLogger(LoggerFactory.getLogger("UserApp"))
                .init();
        DockerContainer container = userApp.container();
        container.portBindings(Arrays.asList(new com.github.lkq.instadocker.docker.entity.PortBinding(restPort)));

        if (container.ensureExists() && container.ensureRunning()) {
            String containerId = container.containerId().get();
            registerUserApp(containerId, restPort, registerURL);
            return containerId;
        } else {
            throw new SmeshException("failed to start user app");
        }
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
}
