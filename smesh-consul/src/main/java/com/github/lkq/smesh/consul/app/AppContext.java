package com.github.lkq.smesh.consul.app;

import com.github.lkq.smesh.consul.config.Config;
import com.github.lkq.smesh.consul.config.ConsulContext;
import com.github.lkq.smesh.docker.SimpleDockerClient;

import java.util.Arrays;

public class AppContext {

    public Config createConfig() {
        return new Config()
                .consulContext(new ConsulContext()
                        .imageName("consul:1.0.6")
                        .hostName("localhost")
                        .nodeName("localhost")
                        .environmentVariables(Arrays.asList("CONSUL_BIND_INTERFACE=eth0"))
                        .portBindings(Arrays.asList())
                        .volumeBindings(Arrays.asList())
                        .commands(Arrays.asList("agent", "-server"))
                );
    }

    public SimpleDockerClient createDockerClient() {
        return SimpleDockerClient.create();
    }
}
