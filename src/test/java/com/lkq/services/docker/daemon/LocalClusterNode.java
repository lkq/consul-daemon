package com.lkq.services.docker.daemon;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.lkq.services.docker.daemon.consul.ConsulController;
import com.lkq.services.docker.daemon.consul.command.AgentCommandBuilder;
import com.lkq.services.docker.daemon.consul.context.ConsulContext;
import com.lkq.services.docker.daemon.consul.context.ConsulContextFactory;
import com.lkq.services.docker.daemon.container.DockerClientFactory;
import com.lkq.services.docker.daemon.container.SimpleDockerClient;
import com.lkq.services.docker.daemon.env.EnvironmentProvider;
import com.lkq.services.docker.daemon.exception.ConsulDaemonException;
import com.lkq.services.docker.daemon.health.ConsulHealthChecker;
import com.lkq.services.docker.daemon.logging.JulToSlf4jBridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class LocalClusterNode {
    private static Logger logger;

    private static String[] NODE_NAMES = {"consul-node0", "consul-node1", "consul-node2"};

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
        String nodeName = NODE_NAMES[nodeIndex];
        AgentCommandBuilder builder = new AgentCommandBuilder()
                .server(true)
                .ui(true)
                .bootstrapExpect(3)
                .clientIP(ConsulContextFactory.BIND_CLIENT_IP)
                .retryJoin(runningNodeIPs(dockerClient));
        ConsulContext context = contextFactory.createDefaultContext(nodeName).commandBuilder(builder);
        if (nodeIndex == 0) {
            context.portBinders(new ConsulPorts().integrationTestPortBindings());
        }
        App app = new App(context, consulController, healthChecker, webServer, "1.2.3");
        app.start(!nodeRunning(dockerClient, nodeName));

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
    }

    private boolean nodeRunning(SimpleDockerClient dockerClient, String nodeName) {
        InspectContainerResponse existingNode = dockerClient.inspectContainer(nodeName);
        return existingNode != null
                && existingNode.getState().getRunning() != null
                && existingNode.getState().getRunning();
    }

    private int nodeIndex() {
        int nodeIndex = Integer.valueOf(System.getProperty("node-index", "-1"));
        if (nodeIndex < 0) {
            throw new ConsulDaemonException("please provide node-name by -Dnode-index=[0, 1, 2]");
        }
        return nodeIndex;
    }

    private List<String> runningNodeIPs(SimpleDockerClient dockerClient) {
        List<String> runningNodeIPs = new ArrayList<>();
        for (String nodeName : NODE_NAMES) {
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
