package com.github.lkq.smesh.logging;

import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.logging.LogManager;

public class JulToSlf4jBridge {
    public static void setup() {
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }
}
