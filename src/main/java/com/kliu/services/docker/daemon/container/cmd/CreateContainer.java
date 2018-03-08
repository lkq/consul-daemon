package com.kliu.services.docker.daemon.container.cmd;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Volume;
import com.kliu.services.docker.daemon.container.PortBinder;
import com.kliu.services.docker.daemon.logging.Timer;
import com.kliu.utils.Guard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateContainer {
    private static Logger logger = LoggerFactory.getLogger(CreateContainer.class);

    private final CreateContainerCmd createContainerCmd;

    public CreateContainer(DockerClient dockerClient, String imageNameTag, String containerName) {
        createContainerCmd = dockerClient.createContainerCmd(imageNameTag).withName(containerName);
    }

    public String exec() {
        final CreateContainerResponse[] createResponse = new CreateContainerResponse[1];
        Timer.log(logger, "created container, image=" + createContainerCmd.getImage() + ", container-ame=" + createContainerCmd.getName(), () -> {
            logger.info("creating container: {}", createContainerCmd.toString());
            createResponse[0] = createContainerCmd.exec();
        });
        return createResponse[0].getId();
    }

    public CreateContainer withConfigVolume(String hostPath) {
        Guard.notBlank(hostPath, "host-path is not provided");
        this.createContainerCmd.withBinds(new Bind(hostPath, new Volume("/consul/config")));
        return this;
    }

    public CreateContainer withDataVolume(String hostPath) {
        Guard.notBlank(hostPath, "host-path is not provided");
        logger.info("data-volume={}", hostPath);
        this.createContainerCmd.withBinds(new Bind(hostPath, new Volume("/consul/data")));
        return this;
    }

    public CreateContainer withEnvironmentVariable(List<String> env) {
        Guard.toBeTrue(env != null, "environment variables are not provided");
        logger.info("environment={}", env);
        this.createContainerCmd.withEnv(env.toArray(new String[0]));
        return this;
    }

    public CreateContainer withCommand(String... cmd) {
        Guard.toBeTrue(cmd != null && cmd.length > 0, "commands are not provided");
        logger.info("command={}", Arrays.asList(cmd));
        this.createContainerCmd.withCmd(cmd);
        return this;
    }

    public CreateContainer withNetwork(String network) {
        Guard.notBlank(network, "network is not provided");
        logger.info("network={}", network);
        this.createContainerCmd.withNetworkMode(network);
        return this;
    }

    public CreateContainer withPortBinders(List<PortBinder> portBinders) {
        List<ExposedPort> exposedPorts = new ArrayList<>();
        Ports bindings = new Ports();
        for (PortBinder portBinder : portBinders) {
            ExposedPort exposedPort = portBinder.getExposedPort();
            exposedPorts.add(exposedPort);
            bindings.bind(exposedPort, portBinder.getPortBinding());
            logger.info("binding port: {}", portBinder);
        }

        createContainerCmd.withExposedPorts(exposedPorts).withPortBindings(bindings);
        return this;
    }
}
