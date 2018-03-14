package com.lkq.services.docker.daemon.consul.context;

import com.kliu.utils.Guard;
import com.lkq.services.docker.daemon.consul.ConsulCommandBuilder;
import com.lkq.services.docker.daemon.container.PortBinder;

import java.util.List;

public class ConsulContext {

    private String imageName;
    private String containerName;
    private String hostName;
    private String network;
    private List<String> environmentVariables;
    private List<PortBinder> portBinders;
    private String dataPath;
    private String[] command;
    private ConsulCommandBuilder commandBuilder;

    public String imageName() {
        return imageName;
    }

    public ConsulContext withImageName(String imageName) {
        this.imageName = imageName;
        return this;
    }

    public String containerName() {
        return containerName;
    }

    public ConsulContext withContainerName(String containerName) {
        this.containerName = containerName;
        return this;
    }

    public String network() {
        return network;
    }

    public ConsulContext withNetwork(String network) {
        this.network = network;
        return this;
    }

    public List<String> environmentVariables() {
        return environmentVariables;
    }

    public ConsulContext withEnvironmentVariables(List<String> environmentVariables) {
        this.environmentVariables = environmentVariables;
        return this;
    }

    public List<PortBinder> portBinders() {
        return portBinders;
    }

    public ConsulContext withPortBinders(List<PortBinder> portBinders) {
        this.portBinders = portBinders;
        return this;
    }

    public String dataPath() {
        return dataPath;
    }

    public ConsulContext withDataPath(String dataPath) {
        this.dataPath = dataPath;
        return this;
    }

    public String hostName() {
        return hostName;
    }

    public ConsulContext withHostName(String hostName) {
        this.hostName = hostName;
        return this;
    }

    public String[] command() {
        if (commandBuilder != null) {
            return commandBuilder.build();
        }
        return command;
    }

    public ConsulContext withCommand(String[] command) {
        Guard.toBeTrue(commandBuilder == null, "command builder already provided");
        this.command = command;
        return this;
    }

    public ConsulCommandBuilder commandBuilder() {
        return commandBuilder;
    }

    public ConsulContext withCommandBuilder(ConsulCommandBuilder commandBuilder) {
        Guard.toBeTrue(command == null || command.length == 0, "command already provided");
        this.commandBuilder = commandBuilder;
        return this;
    }
}
