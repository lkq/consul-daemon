package com.lkq.services.docker.daemon.consul;

import com.lkq.services.docker.daemon.consul.option.SimpleOption;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class ConsulCommandBuilderTest {

    @Test
    void canBuildConsulCommand() {
        String[] command = new ConsulCommandBuilder()
                .with("option1")
                .with(new SimpleOption("option2"))
                .build();

        assertThat(command.length, is(2));
        assertArrayEquals(command, new String[]{"option1", "option2"});
    }
}