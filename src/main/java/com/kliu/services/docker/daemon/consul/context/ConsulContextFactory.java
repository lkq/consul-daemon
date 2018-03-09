package com.kliu.services.docker.daemon.consul.context;

import com.kliu.services.docker.daemon.config.Environment;
import com.kliu.services.docker.daemon.consul.ConsulCommandBuilder;
import com.kliu.services.docker.daemon.consul.option.BootstrapCount;
import com.kliu.services.docker.daemon.consul.option.RetryJoin;
import com.kliu.services.docker.daemon.container.PortBinder;

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
        RetryJoin retryJoin = new RetryJoin(hosts);

        String[] command = createDefaultCommand()
                .with(new BootstrapCount(retryJoin.getHostCount()))
                .with(retryJoin)
                .build();
        return createDefaultContext()
                .withNetwork(HOST_NETWORK)
                .withCommand(command);
    }

    public ConsulContext createMacConsulClusterContext() {
        String[] command = createDefaultCommand()
                .with(new RetryJoin("127.0.0.2 127.0.0.3 127.0.0.4"))
                .build();
        return createDefaultContext()
                .withPortBinders(getPortBinders())
                .withCommand(command);
    }

    public ConsulContext createMacConsulContext() {
        String[] command = createDefaultCommand()
                .with("-client=0.0.0.0")
                .with("-bootstrap")
                .build();
        return createDefaultContext()
                .withPortBinders(getPortBinders())
                .withCommand(command);
    }

    private ConsulContext createDefaultContext() {
        return new ConsulContext()
                .withImageName(CONSUL_IMAGE)
                .withContainerName(CONTAINER_NAME)
                .withEnvironmentVariables(getEnvironmentVariables())
                .withDataPath(Paths.get(".").toAbsolutePath().normalize().toString() + "/data");
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
