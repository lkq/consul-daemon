package com.github.lkq.smesh.linkerd.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class ConfigProcessorTest {

    private ConfigProcessor configProcessor;

    @BeforeEach
    void setUp() {
        configProcessor = new ConfigProcessor();
    }

    @Test
    void canExportConfig() throws IOException {

        HashMap<String, String> variables = new HashMap<>();
        variables.put(com.github.lkq.smesh.linkerd.Constants.VAR_CONSUL_HOST, "172.17.0.2");
        String content = configProcessor.process("/test-template", "smesh-linkerd.yaml", variables, ConfigProcessorTest.class);

        assertThat(content, is(
                "admin:\n" +
                        "  port: 9990\n" +
                        "  ip: 0.0.0.0\n" +
                        "\n" +
                        "routers:\n" +
                        "- protocol: http\n" +
                        "  identifier:\n" +
                        "   kind: io.l5d.path\n" +
                        "   segments: 1\n" +
                        "   consume: true\n" +
                        "  dtab: |\n" +
                        "    /svc => /#/io.l5d.consul/dc1;\n" +
                        "  servers:\n" +
                        "  - port: 8080\n" +
                        "    ip: 0.0.0.0\n" +
                        "\n" +
                        "namers:\n" +
                        "- kind: io.l5d.consul\n" +
                        "  host: 172.17.0.2\n" +
                        "  consistencyMode: stale\n" +
                        "\n" +
                        "telemetry:\n" +
                        "- kind: io.l5d.tracelog\n" +
                        "  sampleRate: 1.0\n" +
                        "  level: TRACE"));
    }
}