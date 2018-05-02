package com.github.lkq.smesh.linkerd.config;

import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigExporterTest {

    private ConfigExporter configExporter;

    @BeforeEach
    void setUp() {
        configExporter = new ConfigExporter();
    }

    @Test
    void canWriteToTempFolder() throws IOException {
        String configPath = ConfigExporterTest.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        String dest = configExporter.export(new LinkerdConfig(), new File(configPath, "config.yaml"));

        assertThat(dest, CoreMatchers.is(configPath + "config.yaml"));
        String content = FileUtils.readFileToString(new File(configPath, "config.yaml"));
        assertTrue(content.startsWith("--- !com.github.lkq.smesh.linkerd.config.LinkerdConfig"));
    }
}