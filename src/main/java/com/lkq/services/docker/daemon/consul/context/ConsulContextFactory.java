package com.lkq.services.docker.daemon.consul.context;

import com.lkq.services.docker.daemon.consul.command.AgentCommandBuilder;
import com.lkq.services.docker.daemon.env.Environment;

import java.util.ArrayList;
import java.util.List;

public class ConsulContextFactory {
    private static final String NET_EASY_HUB = "http://hub-mirror.c.163.com";

    public static final String CONSUL_IMAGE = "consul:1.0.6";
    public static final String CONTAINER_NAME = "consul";
    public static final String HOST_NETWORK = "host";

    public static final int CLUSTER_MEMBER_COUNT = 3;
    public static final String BIND_CLIENT_IP = "0.0.0.0";

    public ConsulContext createClusterNodeContext(String nodeName) {

        AgentCommandBuilder commandBuilder = new AgentCommandBuilder()
                .server(true)
                .bootstrapExpect(CLUSTER_MEMBER_COUNT)
                .retryJoin(Environment.get().clusterMembers());

        return createDefaultContext(nodeName).withCommandBuilder(commandBuilder);
    }

    public ConsulContext createDefaultContext(String nodeName) {
        return new ConsulContext()
                .imageName(CONSUL_IMAGE)
                .nodeName(nodeName)
                .hostName(nodeName)
                .network(Environment.get().getNetwork())
                .dataPath(Environment.get().getDataPath())
                .environmentVariables(getEnvironmentVariables());
    }


    public List<String> getEnvironmentVariables() {
        List<String> env = new ArrayList<>();
        env.add("CONSUL_BIND_INTERFACE=eth0");
        return env;
    }
}
