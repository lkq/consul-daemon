package com.github.lkq.smesh.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AttachLogging {
    private static Logger logger = LoggerFactory.getLogger(AttachLogging.class);

    private final BufferedReader logReader;
    private ArtifactExtractor artifactExtractor;

    private boolean keepGoing = true;

    public static AttachLogging attach(InputStream inputStream, ArtifactExtractor artifactExtractor) {
        AttachLogging attachLogging = new AttachLogging(inputStream, artifactExtractor);
        attachLogging.start();
        return attachLogging;
    }

    public AttachLogging(InputStream inputStream, ArtifactExtractor artifactExtractor) {
        this.logReader = new BufferedReader(new InputStreamReader(inputStream));
        this.artifactExtractor = artifactExtractor;
    }

    private void start() {
        logger.info("attaching mvn logs");
        new Thread(() -> {
            try {
                String line = logReader.readLine();
                while (keepGoing || line != null) {
                    while (line != null) {
                        logger.info(line);
                        artifactExtractor.match(line);
                        line = logReader.readLine();
                    }
                    Thread.sleep(1000);
                    line = logReader.readLine();
                }
            } catch (Exception e) {
                logger.error("failed to read log", e);
            }
        }).start();
    }

    public void stop() {
        keepGoing = false;
    }
}
