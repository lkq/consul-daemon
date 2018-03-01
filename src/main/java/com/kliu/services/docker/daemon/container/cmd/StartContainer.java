package com.kliu.services.docker.daemon.container.cmd;

import com.kliu.services.docker.daemon.container.SimpleDockerClient;
import com.kliu.services.docker.daemon.logging.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartContainer {
    private static Logger logger = LoggerFactory.getLogger(StartContainer.class);

    private SimpleDockerClient simpleDockerClient;

    public StartContainer(SimpleDockerClient simpleDockerClient) {
        this.simpleDockerClient = simpleDockerClient;
    }

    public void exec(String name) {
        Timer.log(logger, "started container " + name, () -> simpleDockerClient.get().startContainerCmd(name).exec());
    }
}
