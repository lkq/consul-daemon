package com.lkq.services.docker.daemon;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.lkq.services.docker.daemon.config.Config;
import com.lkq.services.docker.daemon.consul.ConsulController;
import com.lkq.services.docker.daemon.consul.context.ConsulContext;
import com.lkq.services.docker.daemon.consul.context.ConsulContextFactory;
import com.lkq.services.docker.daemon.consul.option.RetryJoinOption;
import com.lkq.services.docker.daemon.container.SimpleDockerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import spark.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;

public class LaunchClusterMembers {
    private static Logger logger;

    private String[] clusterMembers = {"consul-member1", "consul-member2", "consul-member3"};

    public static void main(String[] args) {
        initLogging();

        logger = LoggerFactory.getLogger(LaunchClusterMembers.class);

        Config.init(new TestConfigProvider());
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

        List<String> memberIPs = new ArrayList<>();
        String nodeName = null;
        for (String member : clusterMembers) {
            InspectContainerResponse memberNode = dockerClient.inspectContainer(member);
            if (memberNode == null || memberNode.getState().getRunning() == null || ! memberNode.getState().getRunning()) {
                // find the first member not running
                if (StringUtils.isEmpty(nodeName)) {
                    nodeName = member;
                }
            } else {
                // collect cluster member ips
                String hostIP = memberNode.getNetworkSettings().getNetworks().get("bridge").getIpAddress();
                logger.info("collecting cluster member ip: {}", hostIP);
                memberIPs.add(hostIP);
            }
        }

        if (StringUtils.isEmpty(nodeName)) {
            throw new RuntimeException("reach max cluster size, please stop a running member first");
        } else {
            logger.info("starting consul cluster member {}", nodeName);
            ConsulContext context = contextFactory.createMacClusterMemberContext(nodeName, clusterMembers.length, RetryJoinOption.fromHosts(memberIPs));
            consulController.start(context);
        }
    }
}
