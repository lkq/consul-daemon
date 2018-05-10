package com.github.lkq.smesh.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AttachLogger {
    private static Logger logger = LoggerFactory.getLogger(AttachLogger.class);

    private final BufferedReader reader;
    private boolean shouldStop = false;

    public static AttachLogger attach(InputStream inputStream) {
        AttachLogger attachLogger = new AttachLogger(inputStream);
        attachLogger.start();
        return attachLogger;
    }

    public AttachLogger(InputStream inputStream) {
        reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    private void start() {
        logger.info("attaching mvn logs");
        new Thread(() -> {
            do {
                try {
                    String line = reader.readLine();
                    if (line != null) {
                        logger.info(line);
                    } else {
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    logger.error("failed to read log", e);
                }
            } while (!shouldStop);
        }).start();
    }

    public void stop() {
        shouldStop = true;
    }
}
