package com.github.lkq.smesh.linkerd.container;

import com.github.lkq.instadocker.InstaDocker;
import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.docker.ContainerLogger;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import com.github.lkq.smesh.linkerd.config.LinkerdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class LinkerdController {
    private static Logger logger = LoggerFactory.getLogger(LinkerdController.class);

    private Logger containerLogger;

    private SimpleDockerClient dockerClient;

    @Inject
    public LinkerdController(@Named("containerLogger") Logger containerLogger, SimpleDockerClient dockerClient) {
        this.containerLogger = containerLogger;
        this.dockerClient = dockerClient;
    }

    public InstaDocker createContainer(LinkerdContext context) {
        logger.info("creating container: {}", context);
        InstaDocker instaDocker = new InstaDocker(context.imageName(), context.hostName())
                .dockerLogger(containerLogger)
                .init();
        instaDocker.container()
                .hostName(context.hostName())
                .volumeBindings(context.volumeBindings())
                .portBindings(context.portBindings())
                .environmentVariables(context.environmentVariables())
                .commands(context.commands())
                .network(context.network());
        return instaDocker;
    }

    public Boolean startNewInstance(ContainerContext context) {
        logger.info("going to start new container from {}", context.imageName());
        dockerClient.pullImage(context.imageName());

        String containerID = dockerClient.createContainer(context.imageName(), context.nodeName())
                .withVolume(context.volumeBindings())
                .withPortBinders(context.portBindings())
                .withCommand(context.commandBuilder().commands())
                .withNetwork(context.network())
                .withHostName(context.hostName())
                .build();

        return dockerClient.startContainer(containerID);
    }

    public void stopAndRemoveExistingInstance(String nodeName) {
        if (dockerClient.containerExists(nodeName)) {
            logger.info("trying to remove existing container: {}", nodeName);
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
        dockerClient.stopContainer(containerName);
    }
}
