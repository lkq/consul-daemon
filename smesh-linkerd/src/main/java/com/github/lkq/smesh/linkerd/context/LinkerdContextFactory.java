package com.github.lkq.smesh.linkerd.context;

import com.github.lkq.smesh.context.ContainerContext;

public class LinkerdContextFactory {

    public static final String IMAGE_NAME = "buoyantio/linkerd:1.3.7";

    public ContainerContext createDefaultContext() {
        return new ContainerContext()
                .imageName(IMAGE_NAME)
                .nodeName("linkerd");
    }

}
