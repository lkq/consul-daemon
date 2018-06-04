package com.github.lkq.smesh.test.app;

import com.github.lkq.smesh.exception.SmeshException;
import com.github.lkq.smesh.test.AttachLogging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import java.io.File;
import java.net.URL;

/**
 * build the UserApp package
 */
public class UserAppPackager {

    private static Logger logger = LoggerFactory.getLogger(UserAppPackager.class);

    public static final String ARTIFACT_PATTERN = "^(.*) Installing (?<path>.*)/(?<name>user-app-\\d+\\.\\d+\\.\\d+.*\\.jar) to .*";

    public String[] buildPackage() {
        try {
            URL classRoot = ClassLoader.getSystemResource("");
            File projectRoot = new File(classRoot.getFile()).getParentFile().getParentFile();
            logger.info("running mvn in {}", projectRoot);
            Process mvn = new ProcessBuilder("mvn", "install", "-DskipTests=true").directory(new File(projectRoot, "user-app")).start();

            ArtifactExtractor extractor = new ArtifactExtractor(ARTIFACT_PATTERN, "path", "name");
            AttachLogging logging = AttachLogging.attach(mvn.getInputStream(), extractor);

            mvn.waitFor();
            logging.stop();

            if (StringUtils.isBlank(extractor.artifactPath()) || StringUtils.isBlank(extractor.artifactName())) {
                throw new IllegalStateException("can not find user-app artifact");
            }
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
