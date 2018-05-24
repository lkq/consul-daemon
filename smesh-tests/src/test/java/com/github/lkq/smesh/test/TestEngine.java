package com.github.lkq.smesh.test;

import com.github.dockerjava.api.command.InspectContainerResponse;
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
import java.util.HashMap;

import static com.github.lkq.smesh.linkerd.Constants.VAR_CONSUL_HOST;

public class TestEngine {
    public static final String USER_APP = "userapp";

    private static Logger logger = LoggerFactory.getLogger(TestEngine.class);

    private final UserAppPackager packager = new UserAppPackager();
    /**
     * if use same DockerClient instance across different threads, it will hang
     */
    private final SimpleDockerClient simpleDockerClient = SimpleDockerClient.create(DockerClientFactory.create());
    private final UserAppImageBuilder imageBuilder = new UserAppImageBuilder(DockerClientFactory.create());
    private App consulApp;
    private com.github.lkq.smesh.linkerd.App linkerdApp;

    public void startEverything() throws IOException, InterruptedException {

        String consul = startConsul(1025);
        String linkerd = startLinkerd(1026, consul);
        String userApp = quickStartUserApp(8081, "ws://127.0.0.1:1025/register", "/Users/kingson/Sandbox/smesh/smesh-tests/target/", "smesh-tests-0.1.0-SNAPSHOT.jar");
    }

    public void stopEverything() {
        consulApp.stop();
        linkerdApp.stop();
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
        consulApp = appMaker.makeApp(
                appPort,
                nodeName,
                ContainerNetwork.CONSUL_MAC,
                serverCommand,
                localDataPath,
                "1.2.3",
                consulClient);

        return consulApp.start(true);
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
        logger.info("test server build success: {}/{}", artifactPath, artifactName);
        String image = imageBuilder.build(artifactPath, artifactName, registerURL);

        if (simpleDockerClient.containerExists(USER_APP)) {
            simpleDockerClient.stopContainer(USER_APP);
            simpleDockerClient.removeContainer(USER_APP);
        }
        String containerId = simpleDockerClient.createContainer(image, USER_APP)
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

    private HashMap<String, String> createLinkerdConfigVariables(String consulContainer) {
        SimpleDockerClient docker = SimpleDockerClient.create();
        InspectContainerResponse consul = docker.inspectContainer(consulContainer);
        String consulIP = consul.getNetworkSettings().getNetworks().get("bridge").getIpAddress();
        HashMap<String, String> configVariables = new HashMap<>();
        configVariables.put(VAR_CONSUL_HOST, consulIP);
        return configVariables;
    }
}
