package com.kliu.services.docker.daemon.container;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.kliu.services.docker.daemon.config.Config;
import com.kliu.services.docker.daemon.container.cmd.CreateContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

public class SimpleDockerClient {
    private static Logger logger = LoggerFactory.getLogger(SimpleDockerClient.class);

    private DockerClient client;

    public SimpleDockerClient() {

        DefaultDockerClientConfig.Builder configBuilder = DefaultDockerClientConfig.createDefaultConfigBuilder();
        configBuilder.withRegistryUrl(Config.getRegistryURL());
        client = DockerClientBuilder.getInstance(configBuilder.build()).build();
    }

    public DockerClient get() {
        return client;
    }

    public boolean isRunning(String containerName) {
        try {
            InspectContainerResponse container = client.inspectContainerCmd(containerName).exec();
            InspectContainerResponse.ContainerState state = container.getState();
            if (state == null || state.getRunning() == null) {
                return false;
            } else {
                return state.getRunning();
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    public boolean containerExists(String containerId) {
        try {
            InspectContainerResponse container = client.inspectContainerCmd(containerId).exec();
            return StringUtils.isNotEmpty(container.getId());
        } catch (Throwable ignored) {
        }
        return false;
    }

    public boolean removeContainer(String containerId) {
        try {
            client.removeContainerCmd(containerId).withForce(true).exec();
            return true;
        } catch (Throwable e) {
            logger.info("failed to remove container: " + containerId, e);
        }
        return false;
    }

    public CreateContainer createContainer(String imageName, String containerName) {
        return new CreateContainer(client, imageName, containerName);
    }

    public void startContainer(String containerID) {
        client.startContainerCmd(containerID).exec();
    }

    public boolean stopContainer(String containerId) {
        try {
            client.stopContainerCmd(containerId).withTimeout(30000).exec();
            return true;
        } catch (NotModifiedException e) {
            logger.info("container [{}] is not running", containerId);
        } catch (Throwable e) {
            logger.info("failed to stop container [" + containerId + "]", e);
        }
        return false;
    }

    public boolean renameContainer(String containerName, String tempContainerName) {
        try {
            client.renameContainerCmd(containerName).withName(tempContainerName).exec();
            return true;
        } catch (Throwable e) {
            logger.info("failed to rename container from [" + containerName + "] to [" + tempContainerName + "]", e);
        }
        return false;
    }
}
