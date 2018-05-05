package com.github.lkq.smesh.consul;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.lkq.smesh.consul.command.ConsulCommandBuilder;
import com.github.lkq.smesh.consul.container.ConsulController;
import com.github.lkq.smesh.consul.context.ConsulContextFactory;
import com.github.lkq.smesh.consul.env.EnvironmentProvider;
import com.github.lkq.smesh.consul.health.ConsulHealthChecker;
import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.docker.DockerClientFactory;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import com.github.lkq.smesh.logging.JulToSlf4jBridge;
import com.github.lkq.smesh.server.WebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * start server nodes and form a cluster when provided -Dnode-index=
 * otherwise start as client node
 */
public class LocalClusterNode {
    public static final int MIN_CLUSTER_SIZE = 3;
    public static final String BIND_CLIENT_IP = "0.0.0.0";

    private static Logger logger;

    public static void main(String[] args) {
        JulToSlf4jBridge.setup();

        logger = LoggerFactory.getLogger(LocalClusterNode.class);

        new LocalClusterNode().start();

        try {
            Object lock = new Object();
            synchronized (lock) {
                lock.wait();
            }
        } catch (InterruptedException e) {
            logger.info("something unexpected", e);
        }
    }

    private void start() {
        EnvironmentProvider.set(new MacEnvironment());
        ConsulHealthChecker healthChecker = mock(ConsulHealthChecker.class);
        given(healthChecker.registeredConsulDaemonVersion()).willReturn("1.2.3");
        WebServer webServer = mock(WebServer.class);

        SimpleDockerClient dockerClient = SimpleDockerClient.create(DockerClientFactory.get());
        ConsulController consulController = new ConsulController(dockerClient);

        ConsulContextFactory contextFactory = new ConsulContextFactory();

        int nodeIndex = nodeIndex();

        boolean isServer = nodeIndex >= 0;
        String nodeName = nodeName(nodeIndex);
        ConsulCommandBuilder builder = new ConsulCommandBuilder()
                .server(isServer)
                .ui(true)
                .clientIP(BIND_CLIENT_IP)
                .retryJoin(runningNodeIPs(dockerClient));
        if (isServer) {
            builder.bootstrapExpect(MIN_CLUSTER_SIZE);
        }
        ContainerContext context = contextFactory.createDefaultContext(nodeName, "", contextFactory.getEnvironmentVariables()).commandBuilder(builder);
        if (nodeIndex == 0) {
            context.portBindings(new ConsulPortBindings().localServerBindings());
        } else if (nodeIndex == -1) {
            context.portBindings(new ConsulPortBindings().localClientBindings());
        }
        App app = new App(context, consulController, healthChecker, webServer, "1.2.3");
        app.start(!nodeRunning(dockerClient, nodeName));

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
    }

    private String nodeName(int nodeIndex) {
        if (nodeIndex >= 0) {
            return "consul-server-" + nodeIndex;
        } else {
            return "consul-client-" + System.currentTimeMillis();
        }
    }

    private boolean nodeRunning(SimpleDockerClient dockerClient, String nodeName) {
        InspectContainerResponse existingNode = dockerClient.inspectContainer(nodeName);
        return existingNode != null
                && existingNode.getState().getRunning() != null
                && existingNode.getState().getRunning();
    }

    private int nodeIndex() {
        String index = System.getProperty("node-index", "");
        if (StringUtils.isNotEmpty(index)) {
            return Integer.valueOf(index);
        } else {
            return -1;
        }
    }

    private List<String> runningNodeIPs(SimpleDockerClient dockerClient) {
        List<String> runningNodeIPs = new ArrayList<>();
        for (int nodeIndex = 0; nodeIndex < MIN_CLUSTER_SIZE; nodeIndex++) {
            String nodeName = "consul-server-" + nodeIndex;
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
