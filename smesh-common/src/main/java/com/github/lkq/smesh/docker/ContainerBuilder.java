package com.github.lkq.smesh.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Volume;
import com.github.lkq.smesh.context.PortBinding;
import com.github.lkq.smesh.context.VolumeBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContainerBuilder {
    private static Logger logger = LoggerFactory.getLogger(ContainerBuilder.class);

    private CreateContainerCmd createContainerCmd;

    public ContainerBuilder(DockerClient dockerClient, String imageNameTag, String containerName) {
        createContainerCmd = dockerClient.createContainerCmd(imageNameTag).withName(containerName);
    }

    public String build() {
        return createContainerCmd.exec().getId();
    }

    public ContainerBuilder withVolume(List<VolumeBinding> volumeBindings) {
        List<Bind> binds = new ArrayList<>();
        for (VolumeBinding volumeBinding : volumeBindings) {
            logger.info("binding volume: {} to {}", volumeBinding.hostPath(), volumeBinding.containerPath());
            binds.add(new Bind(volumeBinding.hostPath(), new Volume(volumeBinding.containerPath())));
        }
        this.createContainerCmd.withBinds(binds);
        return this;
    }

    public ContainerBuilder withEnvironmentVariable(List<String> env) {
        if (env != null && env.size() > 0) {
            logger.info("environment={}", env);
            this.createContainerCmd.withEnv(env.toArray(new String[0]));
        }
        return this;
    }

    public ContainerBuilder withCommand(String... cmd) {
        logger.info("command={}", Arrays.asList(cmd));
        this.createContainerCmd.withCmd(cmd);
        return this;
    }

    public ContainerBuilder withHostName(String hostName) {
        if (StringUtils.isNotEmpty(hostName)) {
            logger.info("hostName={}", hostName);
            this.createContainerCmd.withHostName(hostName);
        }
        return this;
    }

    public ContainerBuilder withNetwork(String network) {
        if (StringUtils.isNotEmpty(network)) {
            logger.info("network={}", network);
            this.createContainerCmd.withNetworkMode(network);
        }
        return this;
    }

    public ContainerBuilder withPortBinders(List<PortBinding> portBindings) {
        if (portBindings != null && portBindings.size() > 0) {
            List<ExposedPort> exposedPorts = new ArrayList<>();
            Ports bindings = new Ports();
            for (PortBinding portBinding : portBindings) {
                ExposedPort exposedPort = portBinding.getExposedPort();
                exposedPorts.add(exposedPort);
                bindings.bind(exposedPort, portBinding.getPortBinding());
                logger.info("binding port: {}", portBinding);
            }

            createContainerCmd.withExposedPorts(exposedPorts).withPortBindings(bindings);
        }
        return this;
    }
}
