package com.lkq.services.docker.daemon.container;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.lkq.services.docker.daemon.logging.Timing;
import com.lkq.services.docker.daemon.logging.TimingProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class SimpleDockerClient {
    private static Logger logger = LoggerFactory.getLogger(SimpleDockerClient.class);

    private DockerClient client;

    public static SimpleDockerClient create(DockerClient client) {
        return TimingProxyFactory.create(new SimpleDockerClient(client));
    }

    /**
     * default constructor required for CGLib proxy
     */
    public SimpleDockerClient() {

    }

    private SimpleDockerClient(DockerClient client) {
        this.client = client;
    }

    public DockerClient get() {
        return client;
    }

    @Timing
    public boolean removeContainer(String containerId) {
        try {
            client.removeContainerCmd(containerId).withForce(true).exec();
            return true;
        } catch (Exception e) {
            logger.info("failed to remove container: " + containerId, e);
        }
        return false;
    }

    @Timing
    public ContainerBuilder createContainer(String imageName, String containerName) {
        return new ContainerBuilder(client, imageName, containerName);
    }

    @Timing
    public Boolean startContainer(String containerID) {
        try {
            client.startContainerCmd(containerID).exec();
            InspectContainerResponse inspectResponse = client.inspectContainerCmd(containerID).exec();
            return inspectResponse.getState().getRunning();
        } catch (Exception e) {
            logger.info("failed to start container: {}", containerID);
        }
        return false;
    }

    @Timing
    public boolean stopContainer(String containerId) {
        try {
            client.stopContainerCmd(containerId).withTimeout(30000).exec();
            logger.info("stopped container: {}", containerId);
            return true;
        } catch (NotModifiedException e) {
            logger.info("container is not running: {}", containerId);
        } catch (Exception e) {
            logger.info("failed to stop container: " + containerId, e);
        }
        return false;
    }

    @Timing
    public boolean renameContainer(String containerName, String newName) {
        try {
            client.renameContainerCmd(containerName).withName(newName).exec();
            return true;
        } catch (Exception e) {
            logger.info("failed to rename container from [" + containerName + "] to [" + newName + "]", e);
        }
        return false;
    }

    public boolean imageExists(String imageName) {
        try {
            InspectImageResponse inspectResponse = client.inspectImageCmd(imageName).exec();
            return StringUtils.isNotEmpty(inspectResponse.getId());
        } catch (Exception e) {
            logger.info("failed to inspect image: " + imageName, e);
        }
        return false;
    }

    @Timing
    public boolean pullImage(String image) {
        try {
            if (!imageExists(image)) {
                client.pullImageCmd(image).exec(new PullImageResultCallback()).awaitCompletion(3000, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            logger.info("failed to pull image: {}, reason: {}", image, e.getMessage());
        }
        return false;
    }

    @Timing
    public InspectContainerResponse inspectContainer(String containerName) {
        try {
            return client.inspectContainerCmd(containerName).exec();
        } catch (Exception e) {
            logger.info("failed to inspect container: {}, reason: {}", containerName, e.getMessage());
        }
        return null;
    }

    @Timing
    public void execute(String containerName, String[] command) {
        try {
            logger.info("executing command, container: {}, command: {}", containerName, command);
            ExecCreateCmdResponse response = client.execCreateCmd(containerName)
                    .withCmd(command)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .exec();
            ByteArrayOutputStream stdout = new ByteArrayOutputStream();
            ByteArrayOutputStream stderr = new ByteArrayOutputStream();
            ExecStartResultCallback resultCallback = client.execStartCmd(response.getId())
                    .exec(new ExecStartResultCallback(stdout, stderr));
            resultCallback.awaitCompletion();
            logger.info("execute result: stdout={}, stderr={}", stdout.toString(), stderr.toString());
        } catch (Exception e) {
            logger.info("failed to execute commands, container: " + containerName + ", command: " + Arrays.toString(command), e);
        }
    }

    public void attachLogging(String containerID, ContainerLogger containerLogger) {

        new Thread(() -> {
            try {
                logger.info("attaching container log: {}", containerID);
                client.logContainerCmd(containerID)
                        .withStdErr(true)
                        .withStdOut(true)
                        .withFollowStream(true)
                        .withTailAll()
                        .exec(containerLogger);
            } catch (Exception e) {
                logger.error("failed to redirect container log");
            }
        }).run();
    }

    public boolean containerExists(String containerId) {
        try {
            InspectContainerResponse container = client.inspectContainerCmd(containerId).exec();
            return StringUtils.isNotEmpty(container.getId());
        } catch (Exception ignored) {
        }
        return false;
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
}
