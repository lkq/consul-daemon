package com.github.lkq.smesh.linkerd;

import com.github.lkq.smesh.context.PortBinding;
import com.github.lkq.smesh.logging.JulToSlf4jBridge;

import java.util.Arrays;
import java.util.List;

public class LocalLinkerdLauncher {
    public static void main(String[] args) {
        JulToSlf4jBridge.setup();
        new LocalLinkerdLauncher().start();
    }

    private void start() {

        List<PortBinding> portBindings = Arrays.asList(new PortBinding(9990, PortBinding.Protocol.TCP),
                new PortBinding(8080, PortBinding.Protocol.TCP));
        String localConfigPath = AppMaker.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        App app = new AppMaker().makeApp("", portBindings, localConfigPath, "1.2.3", 8009);

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        app.start();
    }
}
