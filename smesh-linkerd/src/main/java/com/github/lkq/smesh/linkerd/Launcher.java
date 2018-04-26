package com.github.lkq.smesh.linkerd;

import com.github.lkq.smesh.logging.JulToSlf4jBridge;

public class Launcher {
    public static void main(String[] args) {
        JulToSlf4jBridge.setup();
        new Launcher().start();
    }

    private void start() {
        App app = new App();

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        app.start();
    }

}
