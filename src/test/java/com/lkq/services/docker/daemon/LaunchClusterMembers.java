package com.lkq.services.docker.daemon;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.lkq.services.docker.daemon.consul.option.BootstrapExpectOption;
import com.lkq.services.docker.daemon.env.EnvironmentProvider;
import com.lkq.services.docker.daemon.consul.ConsulController;
import com.lkq.services.docker.daemon.consul.context.ConsulContext;
import com.lkq.services.docker.daemon.consul.context.ConsulContextFactory;
import com.lkq.services.docker.daemon.consul.option.RetryJoinOption;
import com.lkq.services.docker.daemon.container.SimpleDockerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;

public class LaunchClusterMembers {
    private static Logger logger;

    private String[] clusterNodes = {"consul_node0", "consul_node1", "consul_node2"};

    public static void main(String[] args) {
        initLogging();

        logger = LoggerFactory.getLogger(LaunchClusterMembers.class);

        EnvironmentProvider.set(new LocalEnvironment());
        App app = new App();

        ConsulContextFactory contextFactory = new ConsulContextFactory();
        SimpleDockerClient dockerClient = app.getDockerClient();
        ConsulController consulController = app.getConsulController();

        new LaunchClusterMembers().joinCluster(consulController, dockerClient, contextFactory);

        try {
            Object lock = new Object();
            synchronized (lock) {
                lock.wait();
            }
        } catch (InterruptedException e) {
            logger.info("something unexpected", e);
        }
    }

    private static void initLogging() {
        // redirect jul to slf4j
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    private void joinCluster(ConsulController consulController, SimpleDockerClient dockerClient, ConsulContextFactory contextFactory) {

        int nodeIndex = Integer.valueOf(System.getProperty("node-index", "-1"));
        if (nodeIndex < 0) {
            throw new RuntimeException("please provide node-name by -Dnode-index=<node index>");
        }
        String startingNodeName = clusterNodes[nodeIndex];
        List<String> runningNodeIPs = collectRunningNodeIPs(dockerClient, startingNodeName);

        InspectContainerResponse existingNode = dockerClient.inspectContainer(startingNodeName);
        if (existingNode != null && existingNode.getState().getRunning() != null && existingNode.getState().getRunning()) {
            logger.info("attaching logging to existing consul node: {}", startingNodeName);
            consulController.attachLogging(existingNode.getId());
        } else {
            logger.info("starting consul cluster member: {}", startingNodeName);
            ConsulContext context = contextFactory.createDefaultContext(startingNodeName);
            context.commandBuilder()
                    .with("-server")
                    .with("-ui")
                    .with(new BootstrapExpectOption(3))
                    .with(RetryJoinOption.fromHosts(runningNodeIPs));
            if (nodeIndex == 0) {
                context.withPortBinders(new ConsulPorts().getPortBinders());
                context.commandBuilder().with(ConsulContextFactory.BIND_CLIENT_IP);
            }
            consulController.start(context);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> consulController.stop(context.containerName())));
        }
    }

    private List<String> collectRunningNodeIPs(SimpleDockerClient dockerClient, String startingNodeName) {
        List<String> runningNodeIPs = new ArrayList<>();
        for (String nodeName : clusterNodes) {
            if (!nodeName.equals(startingNodeName)) {
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
        }
        return runningNodeIPs;
    }
}
