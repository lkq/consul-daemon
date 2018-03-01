package com.kliu.services.docker.daemon.consul;

import com.github.dockerjava.api.command.InspectContainerResponse;
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

    private final SimpleDockerClient simpleDockerClient = new SimpleDockerClient(null);
    private final String configPath;
    private final String dataPath;

    public ConsulDockerController(String configPath, String dataPath) {
        this.configPath = configPath;
        this.dataPath = dataPath;
    }

    public boolean startConsul() {
        // clean up existing instance
        String containerToDelete = "consul-stopped-" + System.currentTimeMillis();
        boolean renamed = false;
        try {
            new StopContainer(simpleDockerClient).exec(CONSUL_CONTAINER_NAME);
            renamed = new RenameContainer(simpleDockerClient).exec(CONSUL_CONTAINER_NAME, containerToDelete);

            String imageName = new PullImage(simpleDockerClient).exec(CONSUL_IMAGE_TAG, 0);

            Map<String, Object> env = new HashMap<>();
            Map<String, Object> consulLocalConfig = new HashMap<>();
            consulLocalConfig.put("skip_leave_on_interrupt", true);
            env.put("CONSUL_LOCAL_CONFIG", consulLocalConfig);

            String containerID = new CreateContainer(simpleDockerClient, imageName, CONSUL_CONTAINER_NAME)
                    .withConfigVolume(configPath)
                    .withDataVolume(dataPath)
                    .withNetwork("host")
                    .withEnvironmentVariable(env)
                    .withCommand("agent", "-server", "-bootstrap-expect=3", "-retry-join=127.0.0.2")
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
}
