package com.github.lkq.smesh.linkerd.context;

import com.github.lkq.smesh.docker.CommandBuilder;

import java.util.ArrayList;

public class LinkerdCommandBuilder implements CommandBuilder {
    @Override
    public String[] commands() {
        ArrayList<String> commands = new ArrayList<>();
        commands.add("/consul/data/config.yaml");
        return commands.toArray(new String[]{});
    }
}
