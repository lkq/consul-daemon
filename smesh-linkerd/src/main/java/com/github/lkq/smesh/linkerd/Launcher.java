package com.github.lkq.smesh.linkerd;

import com.github.lkq.smesh.logging.JulToSlf4jBridge;

public class Launcher {
    public static void main(String[] args) {
        JulToSlf4jBridge.setup();
        new Launcher().start();
    }

    private void start() {

        String localConfigPath = AppMaker.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        App app = new AppMaker().makeApp("host", null, localConfigPath);

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        app.start();
    }
}
