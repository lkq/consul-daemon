package com.lkq.services.docker.daemon.container.cmd;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.lkq.services.docker.daemon.logging.Timer;
import com.lkq.services.docker.daemon.container.SimpleDockerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartContainer {
    private static Logger logger = LoggerFactory.getLogger(StartContainer.class);

    private SimpleDockerClient simpleDockerClient;

    public StartContainer(SimpleDockerClient simpleDockerClient) {
        this.simpleDockerClient = simpleDockerClient;
    }

    public Boolean exec(String containerID) {
        Timer.log(logger, "started container " + containerID, () -> simpleDockerClient.get().startContainerCmd(containerID).exec());
        InspectContainerResponse inspectResponse = simpleDockerClient.get().inspectContainerCmd(containerID).exec();
        return inspectResponse.getState().getRunning();
    }
}
