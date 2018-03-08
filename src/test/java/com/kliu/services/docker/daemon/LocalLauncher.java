package com.kliu.services.docker.daemon;

import com.kliu.services.docker.daemon.config.Config;
import com.kliu.services.docker.daemon.consul.ConsulContext;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.logging.LogManager;

public class LocalLauncher {
    public static void main(String[] args) {
        initLogging();

        System.setProperty("consul.network.interface", "en1");
//        System.setProperty("consul.cluster.servers", "localhost");
        Config.init(new TestConfigProvider());
        new App().start(new ConsulContext() {
            @Override
            public String getNetwork() {
                return "bridge";
            }
        });
    }

    private static void initLogging() {
        // redirect jul to slf4j
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }
}
