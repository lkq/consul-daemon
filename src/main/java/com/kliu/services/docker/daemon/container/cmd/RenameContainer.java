package com.kliu.services.docker.daemon.container.cmd;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.kliu.services.docker.daemon.container.SimpleDockerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RenameContainer {
    private static Logger logger = LoggerFactory.getLogger(RenameContainer.class);

    private SimpleDockerClient client;

    public RenameContainer(SimpleDockerClient client) {
        this.client = client;
    }

    public boolean exec(String currentName, String newName) {
        try {
            InspectContainerResponse currentContainer = client.get().inspectContainerCmd(currentName).exec();
            logger.info("found container with name={}, containerID={}", currentContainer.getName(), currentContainer.getId());
        } catch (NotFoundException e) {
            logger.info("no existing container with name={}", currentName);
            return false;
        }

        logger.info("renaming container from [{}] to [{}]", currentName, newName);
        client.get().renameContainerCmd(currentName).withName(newName).exec();
        return true;
    }
}
