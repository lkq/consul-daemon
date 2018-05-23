package com.github.lkq.smesh.linkerd.config;

import com.github.lkq.smesh.Constants;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class ConfigExporterTest {

    private ConfigExporter configExporter;

    @BeforeEach
    void setUp() {
        configExporter = new ConfigExporter();
    }

    @Test
    void canExportConfig() throws IOException {
        String configPath = ConfigExporterTest.class.getProtectionDomain().getCodeSource().getLocation().getFile();

        Map config = createSampleConfig(123, 123);

        String configFileName = "config-" + System.currentTimeMillis() + ".yaml";

        String dest = configExporter.writeToFile(new File(configPath, configFileName), config);

        assertThat(dest, is(configPath + configFileName));
        String content = FileUtils.readFileToString(new File(configPath, configFileName), Constants.ENCODING_UTF8);
        assertThat(content, is(
                "admin:\n" +
                "  port: 123\n" +
                "  ip: 0.0.0.0\n" +
                "routers:\n" +
                "- protocol: http\n" +
                "  dtab: /svc => /$/inet/127.1/9990;\n" +
                "  servers:\n" +
                "  - port: 123\n" +
                "    ip: 0.0.0.0\n"));
    }

    @Test
    void canLoadConfig() {
        Map config = configExporter.loadFromResource("smesh-linkerd.yaml");
        Map admin = (Map) config.get("admin");
        assertThat(admin.get("port"), is(9990));
        assertThat(admin.get("ip"), is("0.0.0.0"));
    }

    private Map createSampleConfig(int adminPort, int serverPort) {
        Map config = new LinkedHashMap();
        config.put("admin", ImmutableMap.of("port", adminPort, "ip", "0.0.0.0"));
        config.put("routers", ImmutableList.of(ImmutableMap.of(
                "protocol", "http",
                "dtab", "/svc => /$/inet/127.1/9990;",
                "servers", ImmutableList.of(ImmutableMap.of("port", serverPort, "ip", "0.0.0.0")))));
        return config;
    }
}