package com.lkq.services.docker.daemon.consul.context;

import com.lkq.services.docker.daemon.config.Environment;
import com.lkq.services.docker.daemon.consul.ConsulCommandBuilder;
import com.lkq.services.docker.daemon.consul.option.BootstrapExpectOption;
import com.lkq.services.docker.daemon.consul.option.RetryJoinOption;
import com.lkq.services.docker.daemon.container.PortBinder;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ConsulContextFactory {
    private static final String NET_EASY_HUB = "http://hub-mirror.c.163.com";

    public static final String CONSUL_IMAGE = "consul:1.0.6";
    public static final String CONTAINER_NAME = "consul";
    public static final String HOST_NETWORK = "host";

    public ConsulContext createConsulContext() {
        String hosts = Environment.getEnv("consul.cluster.hosts", "");
        List<RetryJoinOption> retryJoinOptions = RetryJoinOption.fromHosts(hosts);

        String[] command = createDefaultCommand()
                .with(new BootstrapExpectOption(retryJoinOptions.size()))
                .with(retryJoinOptions)
                .build();
        return createDefaultContext(CONTAINER_NAME)
                .withNetwork(HOST_NETWORK)
                .withDataPath(Paths.get(".").toAbsolutePath().normalize().toString() + "/data")
                .withCommand(command);
    }

    public ConsulContext createMacClusterMemberContext(String containerName, int bootstrapCount, List<RetryJoinOption> retryJoinOptions) {
        String[] command = createDefaultCommand()
                .with(retryJoinOptions)
                .with(new BootstrapExpectOption(bootstrapCount))
                .build();
        return createDefaultContext(containerName)
                .withCommand(command);
    }

    public ConsulContext createMacConsulContext() {
        String[] command = createDefaultCommand()
                .with("-client=0.0.0.0")
                .with("-bootstrap")
                .build();
        return createDefaultContext(CONTAINER_NAME)
                .withPortBinders(getPortBinders())
                .withCommand(command);
    }

    private ConsulContext createDefaultContext(String containerName) {
        return new ConsulContext()
                .withImageName(CONSUL_IMAGE)
                .withContainerName(containerName)
                .withEnvironmentVariables(getEnvironmentVariables());
    }

    private ConsulCommandBuilder createDefaultCommand() {
        return new ConsulCommandBuilder()
                .with("agent")
                .with("-server")
                .with("-ui");
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
