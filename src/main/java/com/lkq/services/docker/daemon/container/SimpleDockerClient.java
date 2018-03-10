package com.lkq.services.docker.daemon.container;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.core.command.PullImageResultCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import java.util.concurrent.TimeUnit;

public class SimpleDockerClient {
    private static Logger logger = LoggerFactory.getLogger(SimpleDockerClient.class);

    private DockerClient client;

    public SimpleDockerClient(DockerClient client) {
        this.client = client;
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
        } catch (Exception ignored) {
        }
        return false;
    }

    public boolean containerExists(String containerId) {
        try {
            InspectContainerResponse container = client.inspectContainerCmd(containerId).exec();
            return StringUtils.isNotEmpty(container.getId());
        } catch (Exception ignored) {
        }
        return false;
    }

    public boolean removeContainer(String containerId) {
        try {
            client.removeContainerCmd(containerId).withForce(true).exec();
            return true;
        } catch (Exception e) {
            logger.info("failed to remove container: " + containerId, e);
        }
        return false;
    }

    public ContainerBuilder containerBuilder(String imageName, String containerName) {
        return new ContainerBuilder(client, imageName, containerName);
    }

    public Boolean startContainer(String containerID) {
        try {
            client.startContainerCmd(containerID).exec();
            InspectContainerResponse inspectResponse = client.inspectContainerCmd(containerID).exec();
            return inspectResponse.getState().getRunning();
        } catch (Exception e) {
            logger.info("failed to start ");
        }
        return false;
    }

    public boolean stopContainer(String containerId) {
        try {
            client.stopContainerCmd(containerId).withTimeout(30000).exec();
            return true;
        } catch (NotModifiedException e) {
            logger.info("container [{}] is not running", containerId);
        } catch (Exception e) {
            logger.info("failed to stop container [" + containerId + "]", e);
        }
        return false;
    }

    public boolean renameContainer(String containerName, String newName) {
        try {
            client.renameContainerCmd(containerName).withName(newName).exec();
            return true;
        } catch (Exception e) {
            logger.info("failed to rename container from [" + containerName + "] to [" + newName + "]", e);
        }
        return false;
    }

    public boolean pullImage(String image) {
        try {
            return client.pullImageCmd(image).exec(new PullImageResultCallback()).awaitCompletion(0, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.info("failed to pull image: " + image);
        }
        return false;
    }
}
