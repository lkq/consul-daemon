package com.lkq.services.docker.daemon.consul.context;

import com.lkq.services.docker.daemon.consul.command.AgentCommandBuilder;
import com.lkq.services.docker.daemon.env.Environment;

import java.util.ArrayList;
import java.util.List;

public class ConsulContextFactory {
    private static final String NET_EASY_HUB = "http://hub-mirror.c.163.com";

    public static final String CONSUL_IMAGE = "consul:1.0.6";
    public static final String BIND_CLIENT_IP = "0.0.0.0";

    public ConsulContext createClusterNodeContext() {

        List<String> retryJoin = Environment.get().clusterMembers();
        boolean isServer = Environment.get().isServer();

        AgentCommandBuilder commandBuilder = new AgentCommandBuilder()
                .server(isServer)
                .retryJoin(retryJoin);

        if (isServer) {
            commandBuilder.bootstrapExpect(retryJoin.size());
        }

        return createDefaultContext(Environment.get().nodeName()).commandBuilder(commandBuilder);
    }

    public ConsulContext createDefaultContext(String nodeName) {
        return new ConsulContext()
                .imageName(CONSUL_IMAGE)
                .nodeName(nodeName)
                .hostName(nodeName)
                .network(Environment.get().network())
                .dataPath(Environment.get().dataPath())
                .environmentVariables(getEnvironmentVariables());
    }


    public List<String> getEnvironmentVariables() {
        List<String> env = new ArrayList<>();
        env.add("CONSUL_BIND_INTERFACE=eth0");
        return env;
    }
}
