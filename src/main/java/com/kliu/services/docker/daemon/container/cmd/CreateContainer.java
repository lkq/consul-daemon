package com.kliu.services.docker.daemon.container.cmd;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Volume;
import com.google.gson.Gson;
import com.kliu.services.docker.daemon.container.SimpleDockerClient;
import com.kliu.services.docker.daemon.logging.Timer;
import com.kliu.utils.Guard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreateContainer {
    private static Logger logger = LoggerFactory.getLogger(CreateContainer.class);

    private static Gson gson = new Gson();

    private final CreateContainerCmd createContainerCmd;

    public CreateContainer(SimpleDockerClient simpleDockerClient, String imageNameTag, String containerName) {
        createContainerCmd = simpleDockerClient.get().createContainerCmd(imageNameTag).withName(containerName);
    }

    public String exec() {
        final CreateContainerResponse[] createResponse = new CreateContainerResponse[1];
        Timer.log(logger, "created container, image=" + createContainerCmd.getImage() + ", container name=" + createContainerCmd.getName(), () -> {
            createResponse[0] = createContainerCmd.exec();
        });
        return createResponse[0].getId();
    }

    public CreateContainer withConfigVolume(String hostPath) {
        Guard.notBlank(hostPath, "hostPath is not provided");
        this.createContainerCmd.withBinds(new Bind(hostPath, new Volume("/consul/config")));
        return this;
    }

    public CreateContainer withDataVolume(String hostPath) {
        Guard.notBlank(hostPath, "hostPath is not provided");
        this.createContainerCmd.withBinds(new Bind(hostPath, new Volume("/consul/data")));
        return this;
    }

    public CreateContainer withEnvironmentVariable(Map<String, Object> env) {
        Guard.toBeTrue(env != null, "environment variables are not provided");
        this.createContainerCmd.withEnv(gson.toJson(env));
        return this;
    }

    public CreateContainer withCommand(String... cmd) {
        Guard.toBeTrue(cmd != null && cmd.length > 0, "commands are not provided");
        this.createContainerCmd.withCmd(cmd);
        return this;
    }

    public CreateContainer withNetwork(String network) {
        Guard.notBlank(network, "network is not provided");
        this.createContainerCmd.withNetworkMode(network);
        return this;
    }

    public CreateContainer withBindingHostPorts(int[] tcpPorts, int[] udpPorts) {

        if ((tcpPorts == null || tcpPorts.length == 0) && (udpPorts == null || udpPorts.length == 0)) {
            return this;
        }
        List<ExposedPort> exposedPorts = new ArrayList<>();
        Ports bindings = new Ports();
        if (tcpPorts != null) {
            for (int port : tcpPorts) {
                ExposedPort containerPort = ExposedPort.tcp(port);
                exposedPorts.add(containerPort);
                bindings.bind(containerPort, Ports.Binding.bindPort(port));
            }
        }

        if (udpPorts != null) {
            for (int port : udpPorts) {
                ExposedPort containerPort = ExposedPort.udp(port);
                exposedPorts.add(containerPort);
                bindings.bind(containerPort, Ports.Binding.bindPort(port));
            }
        }
        this.createContainerCmd.withExposedPorts(exposedPorts)
                .withPortBindings(bindings);
        return this;
    }

}
