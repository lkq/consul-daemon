package com.github.lkq.smesh.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Volume;
import com.github.lkq.smesh.StringUtils;
import com.github.lkq.smesh.context.PortBinder;
import com.github.lkq.smesh.context.VolumeBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public ContainerBuilder withVolume(List<VolumeBinder> volumeBinders) {
        List<Bind> binds = new ArrayList<>();
        for (VolumeBinder volumeBinder : volumeBinders) {
            binds.add(new Bind(volumeBinder.hostPath(), new Volume(volumeBinder.containerPath())));
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

    public ContainerBuilder withPortBinders(List<PortBinder> portBinders) {
        if (portBinders != null && portBinders.size() > 0) {
            List<ExposedPort> exposedPorts = new ArrayList<>();
            Ports bindings = new Ports();
            for (PortBinder portBinder : portBinders) {
                ExposedPort exposedPort = portBinder.getExposedPort();
                exposedPorts.add(exposedPort);
                bindings.bind(exposedPort, portBinder.getPortBinding());
                logger.info("binding port: {}", portBinder);
            }

            createContainerCmd.withExposedPorts(exposedPorts).withPortBindings(bindings);
        }
        return this;
    }
}
