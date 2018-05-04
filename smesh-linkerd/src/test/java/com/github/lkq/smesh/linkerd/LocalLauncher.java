package com.github.lkq.smesh.linkerd;

import com.github.lkq.smesh.context.PortBinder;
import com.github.lkq.smesh.logging.JulToSlf4jBridge;

import java.util.Arrays;
import java.util.List;

public class LocalLauncher {
    public static void main(String[] args) {
        JulToSlf4jBridge.setup();
        new LocalLauncher().start();
    }

    private void start() {

        List<PortBinder> portBinders = Arrays.asList(new PortBinder(9990, PortBinder.Protocol.TCP),
                new PortBinder(8080, PortBinder.Protocol.TCP));
        String localConfigPath = AppMaker.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        App app = new AppMaker().makeApp("", portBinders, localConfigPath);

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        app.start();
    }
}
