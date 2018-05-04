package com.github.lkq.smesh.consul.context;

import com.github.lkq.smesh.consul.command.ConsulCommandBuilder;
import com.github.lkq.smesh.consul.env.Environment;
import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.context.VolumeBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConsulContextFactory {
    private static final String NET_EASY_HUB = "http://hub-mirror.c.163.com";

    public static final String CONSUL_IMAGE = "consul:1.0.6";
    public static final String BIND_CLIENT_IP = "0.0.0.0";

    public ContainerContext createClusterNodeContext(String nodeName, String network, boolean runAsServer, List<String> clusterMembers, List<String> env) {

        ConsulCommandBuilder commandBuilder = new ConsulCommandBuilder()
                .server(runAsServer)
                .retryJoin(clusterMembers);

        if (runAsServer) {
            commandBuilder.bootstrapExpect(clusterMembers.size());
        }

        return createDefaultContext(nodeName, network, env).commandBuilder(commandBuilder);
    }

    public ContainerContext createDefaultContext(String nodeName, String network, List<String> env) {
        return new ContainerContext()
                .imageName(CONSUL_IMAGE)
                .nodeName(nodeName)
                .hostName(nodeName)
                .network(network)
                .volumeBinders(Arrays.asList(new VolumeBinding(Environment.get().consulDataPath(), "/consul/data")))
                .environmentVariables(env);
    }


    public List<String> getEnvironmentVariables() {
        List<String> env = new ArrayList<>();
        env.add("CONSUL_BIND_INTERFACE=eth0");
        return env;
    }
}
