package com.lkq.services.docker.daemon.consul.context;

import com.lkq.services.docker.daemon.consul.command.CommandBuilder;
import com.lkq.services.docker.daemon.container.PortBinder;

import java.util.List;

public class ConsulContext {

    private String imageName;
    private String nodeName;
    private String hostName;
    private String network;
    private List<String> environmentVariables;
    private List<PortBinder> portBinders;
    private String dataPath;
    private CommandBuilder commands;

    public String imageName() {
        return imageName;
    }

    public ConsulContext imageName(String imageName) {
        this.imageName = imageName;
        return this;
    }

    public String nodeName() {
        return nodeName;
    }

    public ConsulContext nodeName(String nodeName) {
        this.nodeName = nodeName;
        return this;
    }

    public String network() {
        return network;
    }

    public ConsulContext network(String network) {
        this.network = network;
        return this;
    }

    public List<String> environmentVariables() {
        return environmentVariables;
    }

    public ConsulContext environmentVariables(List<String> environmentVariables) {
        this.environmentVariables = environmentVariables;
        return this;
    }

    public List<PortBinder> portBinders() {
        return portBinders;
    }

    public ConsulContext portBinders(List<PortBinder> portBinders) {
        this.portBinders = portBinders;
        return this;
    }

    public String dataPath() {
        return dataPath;
    }

    public ConsulContext dataPath(String dataPath) {
        this.dataPath = dataPath;
        return this;
    }

    public String hostName() {
        return hostName;
    }

    public ConsulContext hostName(String hostName) {
        this.hostName = hostName;
        return this;
    }

    public CommandBuilder commandBuilder() {
        return commands;
    }

    public ConsulContext commandBuilder(CommandBuilder commandBuilder) {
        this.commands = commandBuilder;
        return this;
    }
}
