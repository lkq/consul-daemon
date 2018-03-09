package com.lkq.services.docker.daemon.container.cmd;

import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.lkq.services.docker.daemon.container.SimpleDockerClient;
import com.lkq.services.docker.daemon.logging.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StopContainer {
    private static Logger logger = LoggerFactory.getLogger(StopContainer.class);

    private SimpleDockerClient simpleDockerClient;

    public StopContainer(SimpleDockerClient simpleDockerClient) {
        this.simpleDockerClient = simpleDockerClient;
    }

    public void exec(String name) {
        try {
            Timer.log(logger, "stopped container " + name, () -> simpleDockerClient.get().stopContainerCmd(name).withTimeout(30).exec());
        } catch (NotFoundException | NotModifiedException ignored) {
        }
    }
}
