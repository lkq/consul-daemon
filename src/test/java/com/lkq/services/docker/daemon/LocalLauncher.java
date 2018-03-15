package com.lkq.services.docker.daemon;

import com.lkq.services.docker.daemon.consul.context.ConsulContext;
import com.lkq.services.docker.daemon.consul.context.ConsulContextFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.logging.LogManager;

public class LocalLauncher {
    public static void main(String[] args) {
        initLogging();

        ConsulContext context = new ConsulContextFactory().createMacConsulContext(ConsulContextFactory.CONTAINER_NAME);
        context.withPortBinders(new ConsulPorts().getPortBinders());
        context.commandBuilder()
                .with("-client=0.0.0.0")
                .with("-bootstrap");
        new App().start(context);
    }

    private static void initLogging() {
        // redirect jul to slf4j
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }
}
