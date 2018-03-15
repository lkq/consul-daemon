package com.lkq.services.docker.daemon.consul.context;

import com.lkq.services.docker.daemon.consul.ConsulCommandBuilder;
import com.lkq.services.docker.daemon.consul.option.BootstrapExpectOption;
import com.lkq.services.docker.daemon.consul.option.RetryJoinOption;
import com.lkq.services.docker.daemon.env.Environment;

import java.util.ArrayList;
import java.util.List;

public class ConsulContextFactory {
    private static final String NET_EASY_HUB = "http://hub-mirror.c.163.com";

    public static final String CONSUL_IMAGE = "consul:1.0.6";
    public static final String CONTAINER_NAME = "consul";
    public static final String HOST_NETWORK = "host";

    public static final int CLUSTER_MEMBER_COUNT = 3;
    public static final String BIND_CLIENT_IP = "-client=0.0.0.0";

    public ConsulContext createConsulClusterNodeContext(String nodeName) {

        ConsulContext context = createDefaultContext(nodeName);
        context.commandBuilder()
                .with("-server")
                .with(new BootstrapExpectOption(CLUSTER_MEMBER_COUNT))
                .with(RetryJoinOption.fromHosts(Environment.get().clusterMembers()));
        return context;
    }

    public ConsulContext createDefaultContext(String nodeName) {
        return new ConsulContext()
                .withImageName(CONSUL_IMAGE)
                .withContainerName(nodeName)
                .withHostName(nodeName)
                .withNetwork(Environment.get().getNetwork())
                .withDataPath(Environment.get().getDataPath())
                .withCommandBuilder(new ConsulCommandBuilder("agent"))
                .withEnvironmentVariables(getEnvironmentVariables());
    }


    public List<String> getEnvironmentVariables() {
        List<String> env = new ArrayList<>();
        env.add("CONSUL_BIND_INTERFACE=eth0");
        return env;
    }
}
