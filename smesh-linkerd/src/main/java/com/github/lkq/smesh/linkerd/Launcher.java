package com.github.lkq.smesh.linkerd;

import com.github.lkq.smesh.AppVersion;
import com.github.lkq.smesh.docker.ContainerNetwork;
import com.github.lkq.smesh.logging.JulToSlf4jBridge;

public class Launcher {
    public static void main(String[] args) {
        JulToSlf4jBridge.setup();
        new Launcher().start();
    }

    private void start() {

        String hostConfigPath = AppMaker.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        App app = new AppMaker().makeApp(0, ContainerNetwork.LINKERD_SERVER, hostConfigPath, AppVersion.get(AppMaker.class));

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        app.start();
    }
}
