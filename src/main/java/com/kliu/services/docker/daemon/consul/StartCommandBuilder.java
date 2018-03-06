package com.kliu.services.docker.daemon.consul;

import com.kliu.services.docker.daemon.consul.option.OptionBuilder;
import com.kliu.services.docker.daemon.consul.option.SimpleOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class StartCommandBuilder {
    private static Logger logger = LoggerFactory.getLogger(StartCommandBuilder.class);

    List<OptionBuilder> optionBuilders = new ArrayList<>();

    public StartCommandBuilder with(OptionBuilder option) {
        optionBuilders.add(option);
        return this;
    }

    public StartCommandBuilder with(String option) {
        optionBuilders.add(new SimpleOption(option));
        return this;
    }

    public String[] build() {
        List<String> commands = new ArrayList<>();
        for (OptionBuilder optionBuilder : optionBuilders) {
            String option = optionBuilder.build();
            if (StringUtils.isNotEmpty(option)) {
                commands.add(option);
            }
        }

        logger.info("consul commands: {}", commands);
        return commands.toArray(new String[0]);
    }
}
