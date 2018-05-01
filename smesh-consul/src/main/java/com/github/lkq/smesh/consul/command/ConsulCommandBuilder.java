package com.github.lkq.smesh.consul.command;

import com.github.lkq.smesh.context.CommandBuilder;
import spark.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConsulCommandBuilder implements CommandBuilder {

    private boolean server;
    private boolean ui;
    private boolean bootstrap;
    private int bootstrapExpect;
    private List<String> retryJoin = new ArrayList<>();
    private String clientIP;

    @Override
    public String[] commands() {
        ArrayList<String> commands = new ArrayList<>();
        commands.add("agent");
        if (server) commands.add("-server");
        if (ui) commands.add("-ui");
        if (bootstrap) commands.add("-bootstrap");
        if (bootstrapExpect > 0) commands.add("-bootstrap-expect=" + bootstrapExpect);
        if (StringUtils.isNotEmpty(clientIP)) commands.add("-client=" + clientIP);
        commands.addAll(retryJoin.stream().map((value) -> "-retry-join=" + value).collect(Collectors.toList()));
        return commands.toArray(new String[]{});
    }

    public ConsulCommandBuilder server(boolean server) {
        this.server = server;
        return this;
    }

    public ConsulCommandBuilder ui(boolean ui) {
        this.ui = ui;
        return this;
    }

    public ConsulCommandBuilder bootstrap(boolean bootstrap) {
        this.bootstrap = bootstrap;
        return this;
    }

    public ConsulCommandBuilder bootstrapExpect(int bootstrapExpect) {
        this.bootstrapExpect = bootstrapExpect;
        return this;
    }

    public ConsulCommandBuilder retryJoin(List<String> retryJoin) {
        this.retryJoin = retryJoin;
        return this;
    }

    public ConsulCommandBuilder clientIP(String client) {
        this.clientIP = client;
        return this;
    }

    @Override
    public String toString() {
        return "ConsulCommandBuilder{" +
                "server=" + server +
                ", ui=" + ui +
                ", bootstrap=" + bootstrap +
                ", bootstrapExpect=" + bootstrapExpect +
                ", retryJoin=" + retryJoin +
                ", clientIP='" + clientIP + '\'' +
                '}';
    }
}
