package com.github.lkq.smesh.linkerd;

import com.github.lkq.smesh.logging.JulToSlf4jBridge;
import org.slf4j.Logger;

public class LocalLauncher {
    private static Logger logger;
    public static void main(String[] args) {
        JulToSlf4jBridge.setup();
        new LocalLauncher().start();
    }

    private void start() {
        App app = new App();

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        app.start();
    }
}
