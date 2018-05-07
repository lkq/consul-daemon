package com.github.lkq.smesh.consul.context;

import com.github.lkq.smesh.consul.Constants;
import com.github.lkq.smesh.consul.command.ConsulCommandBuilder;
import com.github.lkq.smesh.context.ContainerContext;

import java.util.List;

public class ConsulContextFactory {

    public ContainerContext create(String nodeName, String network, List<String> env, ConsulCommandBuilder commandBuilder) {
        return new ContainerContext()
                .imageName(Constants.CONSUL_IMAGE)
                .nodeName(nodeName)
                .hostName(nodeName)
                .network(network)
                .environmentVariables(env)
                .commandBuilder(commandBuilder);
    }

}
