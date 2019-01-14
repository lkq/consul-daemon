package com.github.lkq.smesh.test.app;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * extract the UserApp artifact path and name
 */
public class ArtifactExtractor {

    private final String artifactPathGroup;
    private final String artifactNameGroup;
    private final Pattern pattern;

    private String artifactPath;
    private String artifactName;

    public ArtifactExtractor(String pattern, String artifactPathGroup, String artifactNameGroup) {
        this.pattern = Pattern.compile(pattern);
        this.artifactPathGroup = artifactPathGroup;
        this.artifactNameGroup = artifactNameGroup;
    }

    public void match(String line) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
            artifactPath = matcher.group(artifactPathGroup);
            artifactName = matcher.group(artifactNameGroup);
        }
    }

    public String artifactPath() {
        return artifactPath;
    }

    public String artifactName() {
        return artifactName;
    }

    /**
     * for junit
     */
    public ArtifactExtractor() {
        this(UserAppPackager.INSTALL_ARTIFACT_PATTERN, "path", "name");
    }

    @Test
    void testMatch() {

        ArtifactExtractor extractor = new ArtifactExtractor(UserAppPackager.INSTALL_ARTIFACT_PATTERN, "path", "name");

        List<String> lines = Arrays.asList("[mvn] [INFO] Installing /Users/kingson/Sandbox/smesh/smesh-tests/target/smesh-tests-0.1.0-SNAPSHOT.jar to /Users/kingson/.m2/repository/com/github/lkq/smesh-tests/0.1.0-SNAPSHOT/smesh-tests-0.1.0-SNAPSHOT.jar",
                "[mvn] [INFO] Installing /Users/kingson/Sandbox/smesh/smesh-tests/dependency-reduced-pom.xml to /Users/kingson/.m2/repository/com/github/lkq/smesh-tests/0.1.0-SNAPSHOT/smesh-tests-0.1.0-SNAPSHOT.pom");

        for (String line : lines) {
            extractor.match(line);
        }

        assertThat(artifactPath, CoreMatchers.is("/Users/kingson/Sandbox/smesh/smesh-tests/target"));
        assertThat(artifactName, CoreMatchers.is("smesh-tests-0.1.0-SNAPSHOT.jar"));
    }
}
