package com.github.lkq.smesh.test;

import org.apache.maven.cli.MavenCli;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class TestEngine {

    private static Logger logger = LoggerFactory.getLogger(TestEngine.class);

    public void startEverything() throws IOException, InterruptedException {
        Artifact artifact = buildArtifact();
        logger.info("test server build success: {}/{}", artifact.artifactPath, artifact.artifactName);
    }

    private Artifact buildArtifact() throws IOException, InterruptedException {
        Process mvn = new ProcessBuilder("mvn", "clean", "install", "-DskipTests=true").start();

        ArtifactExtractor extractor = new ArtifactExtractor();
        AttachLogging logging = AttachLogging.attach(mvn.getInputStream(), extractor);

        mvn.waitFor();
        logging.stop();

        return new Artifact(extractor.artifactPath(), extractor.artifactName());
    }

    private void buildWithMvnEmbedder() {
        MavenCli mvn = new MavenCli();
        File pwd = new File("").getAbsoluteFile();

        System.setProperty("maven.multiModuleProjectDirectory", pwd.getParent());
        mvn.doMain(new String[]{"clean", "install"}, pwd.getAbsolutePath(), System.out, System.err);
    }

    class Artifact {
        public Artifact(String artifactPath, String artifactName) {
            this.artifactPath = artifactPath;
            this.artifactName = artifactName;
        }

        public String artifactPath;
        public String artifactName;
    }
}
