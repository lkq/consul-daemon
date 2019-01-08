package com.github.lkq.smesh.linkerd.controller;

import com.github.lkq.instadocker.InstaDocker;
import com.github.lkq.smesh.linkerd.config.LinkerdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class LinkerdController {
    private static Logger logger = LoggerFactory.getLogger(LinkerdController.class);

    private Logger containerLogger;

    @Inject
    public LinkerdController(@Named("containerLogger") Logger containerLogger) {
        this.containerLogger = containerLogger;
    }

    public InstaDocker createContainer(LinkerdContext context) {
        logger.info("creating container: {}", context);
        InstaDocker instaDocker = new InstaDocker(context.imageName(), context.hostName())
                .dockerLogger(containerLogger)
                .init();
        instaDocker.container()
                .hostName(context.hostName())
                .volumeBindings(context.volumeBindings())
                .portBindings(context.portBindings())
                .environmentVariables(context.environmentVariables())
                .commands(context.commands())
                .network(context.network());
        return instaDocker;
    }

}
