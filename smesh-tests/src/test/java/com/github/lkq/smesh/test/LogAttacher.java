package com.github.lkq.smesh.test;

import com.github.lkq.smesh.test.app.ArtifactExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LogAttacher {
    private static Logger logger = LoggerFactory.getLogger(LogAttacher.class);

    private final BufferedReader logReader;
    private ArtifactExtractor artifactExtractor;

    private boolean keepGoing = true;

    public static LogAttacher attach(InputStream inputStream, ArtifactExtractor artifactExtractor) {
        LogAttacher logAttacher = new LogAttacher(inputStream, artifactExtractor);
        logAttacher.start();
        return logAttacher;
    }

    public LogAttacher(InputStream inputStream, ArtifactExtractor artifactExtractor) {
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
