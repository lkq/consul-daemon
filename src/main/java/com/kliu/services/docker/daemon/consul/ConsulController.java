package com.kliu.services.docker.daemon.consul;

import com.kliu.services.docker.daemon.container.ContainerLogRedirector;
import com.kliu.services.docker.daemon.container.SimpleDockerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsulController {

    public static final String CONSUL_CONTAINER_NAME = "consul";
    public static final String CONSUL_IMAGE = "consul:1.0.6";
    private static Logger logger = LoggerFactory.getLogger(ConsulController.class);

    private SimpleDockerClient dockerClient;
    private ConsulHealthChecker consulHealthChecker;
    private ContainerLogRedirector logRedirector;

    public ConsulController(SimpleDockerClient dockerClient,
                            ConsulHealthChecker consulHealthChecker,
                            ContainerLogRedirector logRedirector) {
        this.dockerClient = dockerClient;
        this.consulHealthChecker = consulHealthChecker;
        this.logRedirector = logRedirector;
    }

    public void start(ConsulContext context) {
        if (!consulHealthChecker.isHealthy()) {
            startNewInstance(context);
        } else {
            attachLogging(context.getContainerName());
        }
    }

    private void startNewInstance(ConsulContext context) {
        logger.info("going to start new consul container");
        if (dockerClient.containerExists(context.getContainerName())) {
            /**
             * if an existing consul container is already running, stop and remove it
             * TODO:
             * on consul-daemon startup, it should register its version in the consul instance,
             * when restarting, it should check the registered consul-daemon version,
             * if it's the same, then consul-daemon should attach the log to it without remove and recreate.
             *
             */
            logger.info("found an old consul container, going to stop and remove");
            dockerClient.stopContainer(context.getContainerName());
            String tempContainerName = context.getContainerName() + "-remove-" + System.currentTimeMillis();
            dockerClient.renameContainer(context.getContainerName(), tempContainerName);
            dockerClient.removeContainer(tempContainerName);
        }
        String containerID = dockerClient.createContainer(context.getImageName(), context.getContainerName())
                .withConfigVolume(context.getConfigPath())
                .withDataVolume(context.getDataPath())
                .withNetwork(context.getNetwork())
                .withEnvironmentVariable(context.getEnvironmentVariables())
                .withCommand(context.getStartCommands())
                .withPortBinders(context.getPortBinders())
                .exec();

        dockerClient.startContainer(containerID);
        attachLogging(containerID);
    }

    private void attachLogging(String containerID) {
        logger.info("attaching container log: {}", containerID);
        logRedirector.attach(containerID, dockerClient);
    }
}
