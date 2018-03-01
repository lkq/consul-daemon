package com.kliu.services.docker.daemon.container.cmd;

import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.kliu.services.docker.daemon.container.SimpleDockerClient;
import com.kliu.services.docker.daemon.logging.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class PullImage {
    private static Logger logger = LoggerFactory.getLogger(PullImage.class);

    private SimpleDockerClient simpleDockerClient;

    public PullImage(SimpleDockerClient simpleDockerClient) {
        this.simpleDockerClient = simpleDockerClient;
    }

    public String exec(String name, int timeout) {
        final String[] imageID = {""};
        Timer.log(logger, "pull image " + name, () -> {
            try {
                InspectImageResponse inspectResponse = simpleDockerClient.get().inspectImageCmd(name).exec();
                imageID[0] = inspectResponse.getId();
                logger.info("pull skipped, image already exists: {}", inspectResponse.getRepoTags());
                return;
            } catch (Throwable ignored) {
            }
            try {
                simpleDockerClient.get().pullImageCmd(name).exec(new PullImageResultCallback()).awaitCompletion(timeout > 0 ? timeout : 30, TimeUnit.SECONDS);
                InspectImageResponse inspectResponse = simpleDockerClient.get().inspectImageCmd(name).exec();
                imageID[0] = inspectResponse.getId();
            } catch (Throwable throwable) {
                logger.error("pull failed, image name: {}", name, throwable);
            }
        });
        return imageID[0];
    }
}
