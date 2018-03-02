package com.kliu.services.docker.daemon.consul;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ConsulCommandBuilder {
    private static Logger logger = LoggerFactory.getLogger(ConsulCommandBuilder.class);

    List<String> commands = new ArrayList<>();

    public ConsulCommandBuilder with(String key, String value) {
        logger.info("consul command: {}={}", key, value);
        commands.add(key + "=" + value);
        return this;
    }

    public ConsulCommandBuilder with(String value) {
        logger.info("consul command: {}", value);
        commands.add(value);
        return this;
    }

    public String[] build() {
        return commands.toArray(new String[0]);
    }
}
