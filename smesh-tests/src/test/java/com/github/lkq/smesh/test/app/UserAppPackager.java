package com.github.lkq.smesh.test.app;

import com.github.lkq.smesh.exception.SmeshException;
import com.github.lkq.smesh.test.AttachLogging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * build the UserApp package
 */
public class UserAppPackager {

    private static Logger logger = LoggerFactory.getLogger(UserAppPackager.class);

    public static final String ARTIFACT_PATTERN = "^(.*) Installing (?<path>.*)/(?<name>smesh-tests-\\d+\\.\\d+\\.\\d+.*\\.jar) to .*";

    public String[] buildPackage() {
        try {
            Process mvn = new ProcessBuilder("mvn", "clean", "install", "-DskipTests=true").start();

            ArtifactExtractor extractor = new ArtifactExtractor(ARTIFACT_PATTERN, "path", "name");
            AttachLogging logging = AttachLogging.attach(mvn.getInputStream(), extractor);

            mvn.waitFor();
            logging.stop();

            return new String[]{extractor.artifactPath(), extractor.artifactName()};
        } catch (Exception e) {
            throw new SmeshException("failed to build package", e);
        }
    }

    public static void main(String[] args) {
        String[] artifact = new UserAppPackager().buildPackage();
        logger.info("{}/{}", artifact[0], artifact[1]);
    }
}
