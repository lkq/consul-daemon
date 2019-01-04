package com.github.lkq.smesh.consul;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.lkq.smesh.consul.app.AppContext;
import com.github.lkq.smesh.consul.app.AppModule;
import com.github.lkq.smesh.consul.client.ConsulClient;
import com.github.lkq.smesh.consul.client.ResponseParser;
import com.github.lkq.smesh.consul.client.http.SimpleHttpClient;
import com.github.lkq.smesh.consul.command.ConsulCommandBuilder;
import com.github.lkq.smesh.context.PortBinding;
import com.github.lkq.smesh.docker.ContainerNetwork;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * start server nodes and form a cluster
 */
public class ClusterNode {
    public static final int MIN_CLUSTER_SIZE = 3;
    public static final String BIND_CLIENT_IP = "0.0.0.0";

    private static Logger logger = LoggerFactory.getLogger(ClusterNode.class);
    private final SimpleDockerClient dockerClient;

    public ClusterNode() {
        dockerClient = SimpleDockerClient.create();
    }

    public void startNodes(int nodeIndex, List<PortBinding> portBindings) {

        AppMaker appMaker = new AppMaker();

        try {
            ConsulCommandBuilder serverCommand = ConsulCommandBuilder.server(true, Collections.emptyList())
                    .ui(true)
                    .clientIP(BIND_CLIENT_IP)
                    .retryJoin(runningNodeIPs(dockerClient))
                    .bootstrapExpect(nodeIndex);

            String nodeName = nodeName(nodeIndex);
            logger.info("================ starting server node {} ==================", nodeName);
            String hostDataPath = ClassLoader.getSystemResource(".").getPath() + "data/" + nodeName + "-" + System.currentTimeMillis();

            ConsulClient consulClient = new ConsulClient(new SimpleHttpClient(), new ResponseParser(), "http://localhost:8500");
            App app = appMaker.makeApp(
                    1026 + nodeIndex,
                    nodeName,
                    ContainerNetwork.CONSUL_MAC,
                    serverCommand,
                    hostDataPath,
                    "1.2.3", consulClient);

            Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

            app.start(true);
        } catch (Exception e) {
            logger.error("failed to start cluster node: " + nodeIndex, e);
        }
    }

    public String startNode(int httpPort, AppContext appContext) {
        Main appMain = DaggerMain.builder()
                .appModule(new AppModule(appContext))
                .build();
        com.github.lkq.smesh.consul.app.App app = appMain.app();

        app.start(httpPort);

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
        return app.instaDocker().container().containerId().orElse(null);
    }

    private String nodeName(int nodeIndex) {
        if (nodeIndex >= 0) {
            return "consul-server-" + nodeIndex;
        } else {
            return "consul-client-" + System.currentTimeMillis();
        }
    }

    private List<String> runningNodeIPs(SimpleDockerClient dockerClient) {
        List<String> runningNodeIPs = new ArrayList<>();
        for (int nodeIndex = 0; nodeIndex < MIN_CLUSTER_SIZE; nodeIndex++) {
            String nodeName = nodeName(nodeIndex);
            InspectContainerResponse memberNode = dockerClient.inspectContainer(nodeName);
            if (memberNode != null && memberNode.getState().getRunning() != null && memberNode.getState().getRunning()) {
                // collect cluster member ips
                String nodeIP = memberNode.getNetworkSettings().getNetworks().get("bridge").getIpAddress();
                logger.info("cluster node: {} ip: {}", nodeName, nodeIP);
                runningNodeIPs.add(nodeIP);
            } else {
                logger.info("node not found: {}", nodeName);
            }
        }
        return runningNodeIPs;
    }
}
