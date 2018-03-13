package com.lkq.services.docker.daemon.consul.context;

import com.lkq.services.docker.daemon.config.Config;
import com.lkq.services.docker.daemon.config.Environment;
import com.lkq.services.docker.daemon.consul.ConsulCommandBuilder;
import com.lkq.services.docker.daemon.consul.option.BootstrapExpectOption;
import com.lkq.services.docker.daemon.consul.option.RetryJoinOption;
import com.lkq.services.docker.daemon.container.PortBinder;
import spark.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ConsulContextFactory {
    private static final String NET_EASY_HUB = "http://hub-mirror.c.163.com";

    public static final String CONSUL_IMAGE = "consul:1.0.6";
    public static final String CONTAINER_NAME = "consul";
    public static final String HOST_NETWORK = "host";

    public static final int CLUSTER_MEMBER_COUNT = 3;
    public static final String BIND_CLIENT_IP = "-client=0.0.0.0";

    public ConsulContext createConsulContext() {
        List<RetryJoinOption> retryJoinOptions = new ArrayList<>();
        String retryJoin = Environment.getEnv("consul.cluster.members", "");
        if (StringUtils.isEmpty(retryJoin)) {
            retryJoinOptions.add(new RetryJoinOption("provider=aws tag_key=consul-role tag_value=server"));
        } else {
            retryJoinOptions.addAll(RetryJoinOption.fromHosts(retryJoin));
        }

        String[] command = createDefaultCommand()
                .with(new BootstrapExpectOption(CLUSTER_MEMBER_COUNT))
                .with(retryJoinOptions)
                .build();
        return createDefaultContext(CONTAINER_NAME)
                .withNetwork(HOST_NETWORK)
                .withDataPath(Config.getCurrentPath() + "/data")
                .withCommand(command);
    }

    public ConsulContext createMacClusterMemberContext(String containerName, List<RetryJoinOption> retryJoinOptions, int bootstrapExpectedCount) {
        ConsulCommandBuilder commandBuilder = createDefaultCommand()
                .with(retryJoinOptions)
                .with(new BootstrapExpectOption(bootstrapExpectedCount));
        return createDefaultContext(containerName)
                .withHostName(containerName)
                .withDataPath(Config.getCurrentPath() + "/" + containerName)
                .withCommandBuilder(commandBuilder);
    }

    public ConsulContext createMacConsulContext() {
        ConsulCommandBuilder commandBuilder = createDefaultCommand()
                .with(BIND_CLIENT_IP)
                .with("-bootstrap");
        return createDefaultContext(CONTAINER_NAME)
                .withHostName(CONTAINER_NAME)
                .withPortBinders(getPortBinders())
                .withCommandBuilder(commandBuilder);
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

    public List<PortBinder> getPortBinders() {
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
