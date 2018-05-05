package com.github.lkq.smesh.consul;

import com.github.lkq.smesh.consul.command.ConsulCommandBuilder;
import com.github.lkq.smesh.consul.env.Environment;
import com.github.lkq.smesh.logging.JulToSlf4jBridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class LocalLauncher {
    private static Logger logger;

    public static void main(String[] args) {
        JulToSlf4jBridge.setup();
        logger = LoggerFactory.getLogger(LocalLauncher.class);
        try {
            new LocalLauncher().start();
        } catch (Exception e) {
            logger.error("failed to start application", e);
            System.exit(1);
        }
    }

    private void start() {
        AppMaker appMaker = new AppMaker();

        ConsulCommandBuilder serverCommand = ConsulCommandBuilder.server(true, Collections.emptyList())
                .ui(true)
                .clientIP("0.0.0.0")
                .bootstrap(true);

        App app = appMaker.makeApp("consul", serverCommand, "", ConsulPortBindings.defaultBindings());

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        app.start(Environment.get().forceRestart());
    }
}
