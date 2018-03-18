package com.lkq.services.docker.daemon.consul;

import com.lkq.services.docker.daemon.consul.context.ConsulContext;
import com.lkq.services.docker.daemon.container.ContainerLogger;
import com.lkq.services.docker.daemon.container.SimpleDockerClient;
import com.lkq.services.docker.daemon.health.ConsulHealthChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsulController {

    public static final String CONSUL_CONTAINER_NAME = "consul";
    public static final String CONSUL_IMAGE = "consul:1.0.6";
    private static Logger logger = LoggerFactory.getLogger(ConsulController.class);

    private SimpleDockerClient dockerClient;
    private ConsulHealthChecker consulHealthChecker;

    public ConsulController(SimpleDockerClient dockerClient,
                            ConsulHealthChecker consulHealthChecker) {
        this.dockerClient = dockerClient;
        this.consulHealthChecker = consulHealthChecker;
    }

    public void start(ConsulContext context, boolean forceRestart) {
        if (forceRestart) {
            stopAndRemoveExistingInstance(context.nodeName());
            startNewInstance(context);
        } else if (!consulHealthChecker.isHealthy()) {
            startNewInstance(context);
        }
        attachLogging(context.nodeName());
    }

    public void startNewInstance(ConsulContext context) {
        logger.info("going to start new consul container");
        String containerID = dockerClient.createContainer(context.imageName(), context.nodeName())
                .withDataVolume(context.dataPath())
                .withEnvironmentVariable(context.environmentVariables())
                .withHostName(context.hostName())
                .withNetwork(context.network())
                .withPortBinders(context.portBinders())
                .withCommand(context.commandBuilder().commands())
                .build();

        dockerClient.startContainer(containerID);
    }

    public void stopAndRemoveExistingInstance(String nodeName) {
        if (dockerClient.containerExists(nodeName)) {
            logger.info("removing existing consul container: {}", nodeName);
            dockerClient.stopContainer(nodeName);
            String tempContainerName = nodeName + "-remove-" + System.currentTimeMillis();
            dockerClient.renameContainer(nodeName, tempContainerName);
            dockerClient.removeContainer(tempContainerName);
        }
    }

    public void attachLogging(String containerID) {
        dockerClient.attachLogging(containerID, new ContainerLogger());
    }

    public void stop(String containerName) {
        dockerClient.execute(containerName, new String[]{"consul", "leave"});
        dockerClient.stopContainer(containerName);
    }
}
