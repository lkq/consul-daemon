package com.lkq.services.docker.daemon.consul;

import com.lkq.services.docker.daemon.consul.context.ConsulContext;
import com.lkq.services.docker.daemon.container.ContainerLogger;
import com.lkq.services.docker.daemon.container.SimpleDockerClient;
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
            removeExistingInstance(context);
            startNewInstance(context);
        } else if (!consulHealthChecker.isHealthy()) {
            startNewInstance(context);
        }
        attachLogging(context.nodeName());
    }

    private void startNewInstance(ConsulContext context) {
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

    private void removeExistingInstance(ConsulContext context) {
        if (dockerClient.containerExists(context.nodeName())) {
            /**
             * if an existing consul container is already running, stop and remove it
             * TODO:
             * on consul-daemon startup, it should register its version in the consul instance,
             * when restarting, it should check the registered consul-daemon version,
             * if it's the same, then consul-daemon should attach the log to it without remove and recreate.
             *
             */
            logger.info("found an old consul container, going to stop and remove");
            dockerClient.stopContainer(context.nodeName());
            String tempContainerName = context.nodeName() + "-remove-" + System.currentTimeMillis();
            dockerClient.renameContainer(context.nodeName(), tempContainerName);
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
