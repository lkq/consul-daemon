package com.lkq.services.docker.daemon.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContainerLogRedirector {

    private static Logger logger = LoggerFactory.getLogger(ContainerLogRedirector.class);

    public void attach(String containerID, SimpleDockerClient client) {
        new Thread(() -> {
            try {
                ContainerLogger loggingCallback = new ContainerLogger();

                client.get().logContainerCmd(containerID)
                        .withStdErr(true)
                        .withStdOut(true)
                        .withFollowStream(true)
                        .withTailAll()
                        .exec(loggingCallback);
            } catch (Exception e) {
                logger.error("failed to redirect container log");
            }
        }).run();
    }
}
