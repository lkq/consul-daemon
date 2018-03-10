package com.lkq.services.docker.daemon.consul;

import com.lkq.services.docker.daemon.consul.option.ConsulOption;
import com.lkq.services.docker.daemon.consul.option.SimpleOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ConsulCommandBuilder {
    private static Logger logger = LoggerFactory.getLogger(ConsulCommandBuilder.class);

    List<ConsulOption> options = new ArrayList<>();

    public ConsulCommandBuilder with(ConsulOption option) {
        options.add(option);
        return this;
    }

    public ConsulCommandBuilder with(String option) {
        options.add(new SimpleOption(option));
        return this;
    }

    public ConsulCommandBuilder with(List<? extends ConsulOption> opts) {
        options.addAll(opts);
        return this;
    }

    public String[] build() {

        List<String> commands = new ArrayList<>();
        for (ConsulOption consulOption : options) {
            String option = consulOption.build();
            if (StringUtils.isNotEmpty(option)) {
                commands.add(option);
            }
        }

        logger.info("command: {}", commands);
        return commands.toArray(new String[0]);
    }

}
