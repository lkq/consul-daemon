package com.github.lkq.smesh.linkerd.context;

import com.github.lkq.smesh.context.ContainerContext;

import java.util.ArrayList;
import java.util.List;

public class LinkerdContextFactory {
    private static final String NET_EASY_HUB = "http://hub-mirror.c.163.com";

    public static final String CONSUL_IMAGE = "consul:1.0.6";
    public static final String BIND_CLIENT_IP = "0.0.0.0";

    public ContainerContext createDefaultContext(String nodeName) {
        return new ContainerContext()
                .imageName(CONSUL_IMAGE)
                .nodeName(nodeName)
                .hostName(nodeName)
                .environmentVariables(getEnvironmentVariables());
    }


    public List<String> getEnvironmentVariables() {
        List<String> env = new ArrayList<>();
        env.add("CONSUL_BIND_INTERFACE=eth0");
        return env;
    }
}
