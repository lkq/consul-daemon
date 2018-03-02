package com.kliu.services.docker.daemon.consul;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.google.gson.Gson;
import com.kliu.services.docker.daemon.config.Config;
import com.kliu.services.docker.daemon.container.SimpleDockerClient;
import com.kliu.services.docker.daemon.container.cmd.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ConsulDockerController {

    private static Logger logger = LoggerFactory.getLogger(ConsulDockerController.class);

    public static final String CONSUL_CONTAINER_NAME = "consul";
    public static final String CONSUL_IMAGE_TAG = "consul:1.0.6";
    private static int[] tcpPorts = {8300, 8301, 8302, 8400, 8500};
    private static int[] udpPorts = {8301, 8302};

    private final SimpleDockerClient simpleDockerClient = new SimpleDockerClient();

    public boolean startConsul() {

        // clean up existing instance
        String containerToDelete = "consul-stopped-" + System.currentTimeMillis();
        boolean renamed = false;
        try {
            new StopContainer(simpleDockerClient).exec(CONSUL_CONTAINER_NAME);
            renamed = new RenameContainer(simpleDockerClient).exec(CONSUL_CONTAINER_NAME, containerToDelete);

            String imageName = new PullImage(simpleDockerClient).exec(CONSUL_IMAGE_TAG, 0);

            String[] commands = createCommand();
            Map<String, Object> env = createEnvironmentVariables();

            String containerID = new CreateContainer(simpleDockerClient, imageName, CONSUL_CONTAINER_NAME)
                    .withConfigVolume(Config.getConfigPath())
                    .withDataVolume(Config.getDataPath())
                    .withNetwork(Config.getNetwork())
                    .withEnvironmentVariable(env)
                    .withCommand(commands)
                    .exec();

            new StartContainer(simpleDockerClient).exec(containerID);
            InspectContainerResponse inspectContainerResponse = simpleDockerClient.get().inspectContainerCmd(containerID).exec();
            InspectContainerResponse.ContainerState state = inspectContainerResponse.getState();
            logger.info("container state: {}", state);
            if (state != null && state.getRunning() != null) {
                return state.getRunning();
            } else {
                return false;
            }
        }finally {
            if (renamed) {
                simpleDockerClient.get().removeContainerCmd(containerToDelete).withForce(true).exec();
            }
        }
    }

    private Map<String, Object> createEnvironmentVariables() {
        Map<String, Object> env = new HashMap<>();
        Map<String, Object> consulLocalConfig = new HashMap<>();
        consulLocalConfig.put("skip_leave_on_interrupt", true);
        env.put("CONSUL_LOCAL_CONFIG", consulLocalConfig);

        logger.info("consul docker env variables: {}", new Gson().toJson(env));
        return env;
    }

    private String[] createCommand() {
        ConsulCommandBuilder commandBuilder = new ConsulCommandBuilder()
                .with("agent")
                .with("-server")
                .with("-bootstrap-expect", Config.getBootstrapCount());
        for (String host : Config.getRetryJoin()) {
            commandBuilder.with("-retry-join", host);
        }
        return commandBuilder.build();
    }
}
