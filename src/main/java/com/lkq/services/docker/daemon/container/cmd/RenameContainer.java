package com.lkq.services.docker.daemon.container.cmd;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.lkq.services.docker.daemon.container.SimpleDockerClient;
import com.lkq.services.docker.daemon.logging.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RenameContainer {
    private static Logger logger = LoggerFactory.getLogger(RenameContainer.class);

    private SimpleDockerClient client;

    public RenameContainer(SimpleDockerClient client) {
        this.client = client;
    }

    public boolean exec(String currentName, String newName) {
        final boolean[] renamed = {true};
        Timer.log(logger, "renaming container from " + currentName + " to " + newName, () -> {
            try {
                InspectContainerResponse currentContainer = client.get().inspectContainerCmd(currentName).exec();
                logger.info("found container with name={}, containerID={}", currentContainer.getName(), currentContainer.getId());
            } catch (NotFoundException e) {
                logger.info("no existing container with name={}", currentName);
                renamed[0] = false;
            }

            try {
                logger.info("renaming container from [{}] to [{}]", currentName, newName);
                client.get().renameContainerCmd(currentName).withName(newName).exec();
                client.get().inspectContainerCmd(newName).exec();
            } catch (NotFoundException t) {
                logger.info("rename container failed: {}", currentName);
                renamed[0] = false;
            }
        });

        return renamed[0];
    }
}
