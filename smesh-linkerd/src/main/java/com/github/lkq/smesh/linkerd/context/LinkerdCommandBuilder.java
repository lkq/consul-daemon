package com.github.lkq.smesh.linkerd.context;

import com.github.lkq.smesh.context.CommandBuilder;

import java.util.ArrayList;
import java.util.List;

public class LinkerdCommandBuilder implements CommandBuilder {

    private final List<String> commands;

    public LinkerdCommandBuilder(String containerConfigPath) {
        commands = new ArrayList<>();
        commands.add(containerConfigPath);
    }

    @Override
    public String[] commands() {
        return commands.toArray(new String[]{});
    }
}
