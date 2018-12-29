package com.github.lkq.smesh.consul.container;

import com.github.lkq.instadocker.InstaDocker;
import com.github.lkq.smesh.consul.config.ConsulContext;
import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.docker.ContainerLogger;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class ConsulController {

    private static Logger logger = LoggerFactory.getLogger(ConsulController.class);

    private SimpleDockerClient dockerClient;
    private Logger containerLogger;

    @Inject
    public ConsulController(SimpleDockerClient dockerClient, @Named("containerLogger") Logger containerLogger) {
        this.dockerClient = dockerClient;
        this.containerLogger = containerLogger;
    }

    public InstaDocker createContainer(ConsulContext context) {
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
        logger.info("starting new container instance: {}", context);
        dockerClient.pullImage(context.imageName());

        String containerID = dockerClient.createContainer(context.imageName(), context.nodeName())
                .withVolume(context.volumeBindings())
                .withEnvironmentVariable(context.environmentVariables())
                .withHostName(context.hostName())
                .withNetwork(context.network())
                .withPortBinders(context.portBindings())
                .withCommand(context.commandBuilder().commands())
                .build();

        return dockerClient.startContainer(containerID);
    }

    public void stopAndRemoveExistingInstance(String nodeName) {
        if (dockerClient.containerExists(nodeName)) {
            logger.info("found existing container: {}", nodeName);
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
//        dockerClient.execute(containerName, new String[]{"consul", "leave"});
        dockerClient.stopContainer(containerName);
    }
}
