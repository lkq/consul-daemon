package com.github.lkq.smesh.consul.command;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class AgentCommandBuilderTest {
    @Test
    void canBuildAgentCommand() {
        AgentCommandBuilder builder = new AgentCommandBuilder();
        builder.server(true)
                .ui(true)
                .bootstrap(true)
                .retryJoin(Arrays.asList("1.2.3.4"))
                .clientIP("1.1.1.1")
                .bootstrapExpect(3);

        String[] commands = builder.commands();
        Assertions.assertArrayEquals(commands, new String[]{"agent", "-server", "-ui", "-bootstrap",
                "-bootstrap-expect=3", "-client=1.1.1.1", "-retry-join=1.2.3.4"});
    }
}