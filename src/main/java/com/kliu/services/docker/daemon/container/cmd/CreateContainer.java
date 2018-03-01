package com.kliu.services.docker.daemon.container.cmd;

import com.github.dockerjava.api.command.CreateContainerResponse;
import com.kliu.services.docker.daemon.container.SimpleDockerClient;
import com.kliu.services.docker.daemon.logging.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateContainer {
    private static Logger logger = LoggerFactory.getLogger(CreateContainer.class);

    private SimpleDockerClient simpleDockerClient;

    public CreateContainer(SimpleDockerClient simpleDockerClient) {
        this.simpleDockerClient = simpleDockerClient;
    }

    public String exec(String image, String name) {
        final CreateContainerResponse[] createResponse = new CreateContainerResponse[1];
        Timer.log(logger, "created container, image=" + image + ", name=" + name, () -> createResponse[0] = simpleDockerClient.get().createContainerCmd(image).withName(name).exec());
        return createResponse[0].getId();
    }
}
