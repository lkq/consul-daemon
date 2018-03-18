package com.lkq.services.docker.daemon;

import com.lkq.services.docker.daemon.consul.command.AgentCommandBuilder;
import com.lkq.services.docker.daemon.consul.context.ConsulContext;
import com.lkq.services.docker.daemon.consul.context.ConsulContextFactory;
import com.lkq.services.docker.daemon.env.Environment;
import com.lkq.services.docker.daemon.env.EnvironmentProvider;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.logging.LogManager;

public class LocalLauncher {
    public static void main(String[] args) {
        initLogging();

        EnvironmentProvider.set(new MacEnvironment());

        AgentCommandBuilder builder = new AgentCommandBuilder()
                .server(true)
                .ui(true)
                .clientIP("0.0.0.0")
                .bootstrap(true);
        ConsulContext context = new ConsulContextFactory().createDefaultContext(Environment.get().nodeName());
        context.portBinders(new ConsulPorts().getPortBinders());
        context.withCommandBuilder(builder);
        App app = new App(context);
        app.start();

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
    }

    private static void initLogging() {
        // redirect jul to slf4j
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }
}
