package com.github.lkq.smesh.consul.profile;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class ProfileFactory {

    public static final String ARTIFACT_NAME = "artifact-name";
    public static final String ARTIFACT_VERSION = "artifact-version";

    private String nodeName;
    private String artifactName;
    private String artifactVersion;

    public ProfileFactory(Class mainClass, String nodeName) throws IOException {
        this(mainClass.getProtectionDomain().getCodeSource().getLocation().getPath(), nodeName);
    }

    public ProfileFactory(String filePath, String nodeName) throws IOException {
        this.nodeName = nodeName;

        Manifest manifest = new JarFile(filePath).getManifest();
        Attributes mainAttr = manifest.getMainAttributes();
        artifactName = Optional.ofNullable(mainAttr.getValue(ARTIFACT_NAME)).orElse("N/A");
        artifactVersion = Optional.ofNullable(mainAttr.getValue(ARTIFACT_VERSION)).orElse("N/A");
    }

    public Profile create() {
        long millis = ManagementFactory.getRuntimeMXBean().getUptime();

        return new Profile()
                .name(artifactName)
                .version(artifactVersion)
                .nodeName(nodeName)
                .millis(millis);
    }
}
