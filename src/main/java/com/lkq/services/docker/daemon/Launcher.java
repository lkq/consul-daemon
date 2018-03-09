package com.lkq.services.docker.daemon;

import com.lkq.services.docker.daemon.config.Config;
import com.lkq.services.docker.daemon.config.ConfigProvider;
import com.lkq.services.docker.daemon.consul.context.ConsulContext;
import com.lkq.services.docker.daemon.consul.context.ConsulContextFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.logging.LogManager;

public class Launcher {
    public static void main(String[] args) {
        initLogging();
        Config.init(new ConfigProvider());
        ConsulContext context = new ConsulContextFactory().createConsulContext();
        new App().start(context);
    }

    private static void initLogging() {
        // redirect jul to slf4j
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }
}
