package com.github.lkq.smesh.linkerd;

import com.github.lkq.smesh.AppVersion;
import com.github.lkq.smesh.docker.ContainerNetwork;
import com.github.lkq.smesh.logging.JulToSlf4jBridge;

import java.util.HashMap;

import static com.github.lkq.smesh.linkerd.Constants.VAR_CONSUL_HOST;

public class Launcher {
    public static void main(String[] args) {
        JulToSlf4jBridge.setup();
        new Launcher().start();
    }

    private void start() {

        String hostConfigPath = AppMaker.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        HashMap<String, String> configVariables = new HashMap<>();
        configVariables.put(VAR_CONSUL_HOST, "127.0.0.1");
        App app = new AppMaker().makeApp(0, ContainerNetwork.LINKERD_SERVER, configVariables, hostConfigPath, AppVersion.get(AppMaker.class));

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        app.start();
    }
}
