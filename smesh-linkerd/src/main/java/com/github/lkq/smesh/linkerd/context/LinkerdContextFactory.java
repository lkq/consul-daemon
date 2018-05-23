package com.github.lkq.smesh.linkerd.context;

import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.linkerd.Constants;

public class LinkerdContextFactory {


    public ContainerContext createDefaultContext() {
        return new ContainerContext()
                .imageName(Constants.LINKERD_IMAGE)
                .nodeName("linkerd");
    }

}
