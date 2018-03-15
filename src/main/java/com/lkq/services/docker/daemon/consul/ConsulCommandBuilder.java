package com.lkq.services.docker.daemon.consul;

import com.lkq.services.docker.daemon.consul.option.ConsulOption;
import com.lkq.services.docker.daemon.consul.option.SimpleOption;
import spark.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ConsulCommandBuilder {

    List<ConsulOption> options = new ArrayList<>();

    public ConsulCommandBuilder(String... cmd) {
        for (String c : cmd) {
            if (StringUtils.isNotEmpty(c)) {
                options.add(new SimpleOption(c));
            }
        }
    }

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

        return commands.toArray(new String[0]);
    }

}
