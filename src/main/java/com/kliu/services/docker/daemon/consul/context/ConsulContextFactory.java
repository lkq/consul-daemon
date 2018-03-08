package com.kliu.services.docker.daemon.consul.context;

import com.kliu.services.docker.daemon.config.env.Platform;
import com.kliu.services.docker.daemon.consul.ConsulCommandBuilder;
import com.kliu.services.docker.daemon.container.PortBinder;
import com.kliu.services.docker.daemon.consul.option.BootstrapCount;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ConsulContextFactory {

    public static final String CONSUL_IMAGE = "consul:1.0.6";
    public static final String CONTAINER_NAME = "consul";
    public static final String HOST_NETWORK = "host";

    public ConsulContext create() {
        switch (Platform.get()) {
            case LINUX_AWS:
                return createAwsConsulContext();
            case MAC:
                return createMacConsulContext();
            case LINUX:
                return createLinuxConsulContext();
            default:
                throw new RuntimeException("unsupported platform: " + Platform.get());
        }
    }

    private ConsulContext createLinuxConsulContext() {

        String[] command = new ConsulCommandBuilder()
                .with("agent")
                .with("-server")
                .with("-ui")
                .with(new BootstrapCount(3))
                .build();
        return new ConsulContext()
                .withImageName(CONSUL_IMAGE)
                .withContainerName(CONTAINER_NAME)
                .withEnvironmentVariables(getEnvironmentVariables())
                .withDataPath(Paths.get(".").toAbsolutePath().normalize().toString() + "/data")
                .withNetwork(HOST_NETWORK)
                .withCommand(command);
    }

    private ConsulContext createMacConsulContext() {
        String[] command = new ConsulCommandBuilder()
                .with("agent")
                .with("-server")
                .with("-ui")
                .with(new BootstrapCount(3))
                .build();
        return new ConsulContext()
                .withImageName(CONSUL_IMAGE)
                .withContainerName(CONTAINER_NAME)
                .withEnvironmentVariables(getEnvironmentVariables())
                .withDataPath(Paths.get(".").toAbsolutePath().normalize().toString() + "/data")
                .withCommand(command);
    }

    private ConsulContext createAwsConsulContext() {
        String[] command = new ConsulCommandBuilder()
                .with("agent")
                .with("-server")
                .with("-ui")
                .with(new BootstrapCount(3))
                .build();
        return new ConsulContext()
                .withImageName(CONSUL_IMAGE)
                .withContainerName(CONTAINER_NAME)
                .withEnvironmentVariables(getEnvironmentVariables())
                .withDataPath(Paths.get(".").toAbsolutePath().normalize().toString() + "/data")
                .withCommand(command);
    }

    private List<PortBinder> getPortBinders() {
        List<PortBinder> portBinders = new ArrayList<>();
        portBinders.add(new PortBinder(8300, PortBinder.Protocol.TCP));
        portBinders.add(new PortBinder(8301, PortBinder.Protocol.TCP));
        portBinders.add(new PortBinder(8302, PortBinder.Protocol.TCP));
        portBinders.add(new PortBinder(8400, PortBinder.Protocol.TCP));
        portBinders.add(new PortBinder(8500, PortBinder.Protocol.TCP));
        portBinders.add(new PortBinder(8301, PortBinder.Protocol.UDP));
        portBinders.add(new PortBinder(8302, PortBinder.Protocol.UDP));
        return portBinders;
    }

    public List<String> getEnvironmentVariables() {
        List<String> env = new ArrayList<>();
        env.add("CONSUL_BIND_INTERFACE=eth0");
        return env;
    }
}
