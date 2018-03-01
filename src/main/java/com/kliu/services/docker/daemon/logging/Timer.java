package com.kliu.services.docker.daemon.logging;

import org.slf4j.Logger;

public class Timer {
    public static void log(Logger logger, String message, Runnable runnable) {
        long startTime = System.currentTimeMillis();
        runnable.run();
        logger.info(message + " in {} ms", System.currentTimeMillis() - startTime);
    }
}
