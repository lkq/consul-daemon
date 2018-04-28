package com.github.lkq.smesh.linkerd.context;

import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.docker.PortBinder;

import java.util.Arrays;

public class LinkerdContextFactory {
    private static final String NET_EASY_HUB = "http://hub-mirror.c.163.com";

    public static final String IMAGE_NAME = "buoyantio/linkerd:1.3.7";

    public ContainerContext createDefaultContext() {
        return new ContainerContext()
                .imageName(IMAGE_NAME)
                .nodeName("linkerd")
                .dataPath("/Users/kingson/Sandbox/smesh/smesh-linkerd/src/main/resources")
                .portBinders(Arrays.asList(new PortBinder(9990, 9990, PortBinder.Protocol.TCP)));
    }

}
