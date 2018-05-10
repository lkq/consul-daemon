package com.github.lkq.smesh.test;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;

public class ArtifactExtractor {

    private Pattern pattern;
    private String artifactPath;
    private String artifactName;

    public ArtifactExtractor() {
        pattern = Pattern.compile("^(.*) Installing (?<path>.*)/(?<name>smesh-tests-\\d+\\.\\d+\\.\\d+.*\\.jar) to .*");
    }

    public void match(String line) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
            artifactPath = matcher.group("path");
            artifactName = matcher.group("name");
        }
    }

    public String artifactPath() {
        return artifactPath;
    }

    public String artifactName() {
        return artifactName;
    }

    @Test
    void canCaptureArtifactPath() {
        List<String> lines = Arrays.asList("[mvn] [INFO] Installing /Users/kingson/Sandbox/smesh/smesh-tests/target/smesh-tests-0.1.0-SNAPSHOT.jar to /Users/kingson/.m2/repository/com/github/lkq/smesh-tests/0.1.0-SNAPSHOT/smesh-tests-0.1.0-SNAPSHOT.jar",
                "[mvn] [INFO] Installing /Users/kingson/Sandbox/smesh/smesh-tests/dependency-reduced-pom.xml to /Users/kingson/.m2/repository/com/github/lkq/smesh-tests/0.1.0-SNAPSHOT/smesh-tests-0.1.0-SNAPSHOT.pom");

        for (String line : lines) {
            match(line);
        }

        assertThat(artifactPath, CoreMatchers.is("/Users/kingson/Sandbox/smesh/smesh-tests/target"));
        assertThat(artifactName, CoreMatchers.is("smesh-tests-0.1.0-SNAPSHOT.jar"));
    }
}
