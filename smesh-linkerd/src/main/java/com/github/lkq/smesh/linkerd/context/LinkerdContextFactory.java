package com.github.lkq.smesh.linkerd.context;

import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.context.PortBinder;
import com.github.lkq.smesh.context.VolumeBinder;

import java.util.Arrays;

public class LinkerdContextFactory {
    private static final String NET_EASY_HUB = "http://hub-mirror.c.163.com";

    public static final String IMAGE_NAME = "buoyantio/linkerd:1.3.7";

    public ContainerContext createDefaultContext() {
        return new ContainerContext()
                .imageName(IMAGE_NAME)
                .nodeName("linkerd")
                .volumeBinders(Arrays.asList(new VolumeBinder("/Users/kingson/Sandbox/smesh/smesh-linkerd/src/main/resources", "/config")))
                .portBinders(Arrays.asList(new PortBinder(9990, 9990, PortBinder.Protocol.TCP)))
                .attachStdIn(true);
    }

}
