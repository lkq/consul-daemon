package com.github.lkq.smesh.linkerd;

import com.github.lkq.smesh.docker.ContainerNetwork;
import com.github.lkq.smesh.logging.JulToSlf4jBridge;

import java.util.HashMap;

import static com.github.lkq.smesh.linkerd.Constants.VAR_CONSUL_HOST;

public class LocalLinkerdLauncher {
    public static void main(String[] args) {
        JulToSlf4jBridge.setup();
        new LocalLinkerdLauncher().start();
    }

    private void start() {

        String localConfigPath = AppMaker.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        HashMap<String, String> configVariables = new HashMap<>();
        configVariables.put(VAR_CONSUL_HOST, "127.0.0.1");

        App app = new AppMaker().makeApp(1026, ContainerNetwork.LINKERD_MAC, configVariables, localConfigPath, "1.2.3");

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        app.start();
    }
}
