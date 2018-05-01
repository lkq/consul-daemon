package com.github.lkq.smesh.consul.context;

import com.github.lkq.smesh.consul.command.ConsulCommandBuilder;
import com.github.lkq.smesh.consul.env.Environment;
import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.docker.VolumeBinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConsulContextFactory {
    private static final String NET_EASY_HUB = "http://hub-mirror.c.163.com";

    public static final String CONSUL_IMAGE = "consul:1.0.6";
    public static final String BIND_CLIENT_IP = "0.0.0.0";

    public ContainerContext createClusterNodeContext() {

        List<String> retryJoin = Environment.get().clusterMembers();
        boolean isServer = Environment.get().isServer();

        ConsulCommandBuilder commandBuilder = new ConsulCommandBuilder()
                .server(isServer)
                .retryJoin(retryJoin);

        if (isServer) {
            commandBuilder.bootstrapExpect(retryJoin.size());
        }

        return createDefaultContext(Environment.get().nodeName()).commandBuilder(commandBuilder);
    }

    public ContainerContext createDefaultContext(String nodeName) {
        return new ContainerContext()
                .imageName(CONSUL_IMAGE)
                .nodeName(nodeName)
                .hostName(nodeName)
                .network(Environment.get().network())
                .volumeBinders(Arrays.asList(new VolumeBinder(Environment.get().consulDataPath(), "/consul/data")))
                .environmentVariables(getEnvironmentVariables());
    }


    public List<String> getEnvironmentVariables() {
        List<String> env = new ArrayList<>();
        env.add("CONSUL_BIND_INTERFACE=eth0");
        return env;
    }
}
