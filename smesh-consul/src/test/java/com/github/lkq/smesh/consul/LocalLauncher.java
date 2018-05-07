package com.github.lkq.smesh.consul;

import com.github.lkq.smesh.consul.command.ConsulCommandBuilder;
import com.github.lkq.smesh.consul.env.Environment;
import com.github.lkq.smesh.logging.JulToSlf4jBridge;

import java.util.Collections;

public class LocalLauncher {

    public static void main(String[] args) {
        JulToSlf4jBridge.setup();
        new LocalLauncher().start();
    }

    public void start() {
        AppMaker appMaker = new AppMaker();

        ConsulCommandBuilder serverCommand = ConsulCommandBuilder.server(true, Collections.emptyList())
                .ui(true)
                .clientIP("0.0.0.0")
                .bootstrap(true);

        String nodeName = "consul";
        String localDataPath = ClassLoader.getSystemResource(".").getPath() + "data/" + nodeName + "-" + System.currentTimeMillis();
        App app = appMaker.makeApp(nodeName, serverCommand, "", ConsulPortBindings.defaultBindings(), "1.2.3", 1025, localDataPath);

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        app.start(Environment.get().forceRestart());
    }
}
