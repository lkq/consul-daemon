package com.github.lkq.smesh.test;

import org.apache.maven.cli.MavenCli;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class TestEngine {

    private static Logger logger = LoggerFactory.getLogger(TestEngine.class);

    public void startEverything() throws IOException, InterruptedException {
        buildWithMvn();
    }

    private void buildWithMvn() throws IOException, InterruptedException {
        Process mvn = new ProcessBuilder("mvn", "clean", "install", "-DskipTests=true").start();
        AttachLogger attachLogger = AttachLogger.attach(mvn.getInputStream());
        mvn.waitFor();
        attachLogger.stop();
    }

    private void buildWithMvnEmbedder() {
        MavenCli mvn = new MavenCli();
        File pwd = new File("").getAbsoluteFile();

        System.setProperty("maven.multiModuleProjectDirectory", pwd.getParent());
        mvn.doMain(new String[]{"clean", "install"}, pwd.getAbsolutePath(), System.out, System.err);
    }
}
