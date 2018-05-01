package com.github.lkq.smesh.consul.container;

import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.docker.ContainerLogger;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsulController {

    private static Logger logger = LoggerFactory.getLogger(ConsulController.class);

    private SimpleDockerClient dockerClient;

    public ConsulController(SimpleDockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    public Boolean startNewInstance(ContainerContext context) {
        logger.info("going to start new container from {}", context.imageName());
        dockerClient.pullImage(context.imageName());

        String containerID = dockerClient.createContainer(context.imageName(), context.nodeName())
                .withVolume(context.volumeBinders())
                .withEnvironmentVariable(context.environmentVariables())
                .withHostName(context.hostName())
                .withNetwork(context.network())
                .withPortBinders(context.portBinders())
                .withCommand(context.commandBuilder().commands())
                .build();

        return dockerClient.startContainer(containerID);
    }

    public void stopAndRemoveExistingInstance(String nodeName) {
        if (dockerClient.containerExists(nodeName)) {
            logger.info("removing existing container: {}", nodeName);
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
