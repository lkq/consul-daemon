package com.github.lkq.smesh.linkerd.context;

import com.github.lkq.smesh.docker.CommandBuilder;

import java.util.ArrayList;

public class LinkerdCommandBuilder implements CommandBuilder {
    private String configContent;

    @Override
    public String[] commands() {
        ArrayList<String> commands = new ArrayList<>();
        commands.add("/config/config.yaml");
//        commands.add("-");
//        commands.add(configContent);
        return commands.toArray(new String[]{});
    }

    public LinkerdCommandBuilder config(String linkerdConfig) {
        this.configContent = linkerdConfig;
        return this;
    }
}
