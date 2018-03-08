package com.kliu.services.docker.daemon;

import com.kliu.services.docker.daemon.config.Config;
import com.kliu.services.docker.daemon.config.ConfigProvider;
import com.kliu.services.docker.daemon.consul.ConsulContext;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.logging.LogManager;

public class Launcher {
    public static void main(String[] args) {
        initLogging();
        Config.init(new ConfigProvider());
        new App().start(new ConsulContext());
    }

    private static void initLogging() {
        // redirect jul to slf4j
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }
}
