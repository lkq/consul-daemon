package com.github.lkq.smesh.linkerd;

import com.github.lkq.smesh.logging.JulToSlf4jBridge;
import org.slf4j.Logger;

public class LocalLauncher {
    private static Logger logger;
    public static void main(String[] args) {
        JulToSlf4jBridge.setup();
        Launcher.main(args);
    }
}
