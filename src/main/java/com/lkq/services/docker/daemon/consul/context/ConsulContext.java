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

    public String getImageName() {
        return imageName;
    }

    public ConsulContext withImageName(String imageName) {
        this.imageName = imageName;
        return this;
    }

    public String getContainerName() {
        return containerName;
    }

    public ConsulContext withContainerName(String containerName) {
        this.containerName = containerName;
        return this;
    }

    public String getNetwork() {
        return network;
    }

    public ConsulContext withNetwork(String network) {
        this.network = network;
        return this;
    }

    public List<String> getEnvironmentVariables() {
        return environmentVariables;
    }

    public ConsulContext withEnvironmentVariables(List<String> environmentVariables) {
        this.environmentVariables = environmentVariables;
        return this;
    }

    public List<PortBinder> getPortBinders() {
        return portBinders;
    }

    public ConsulContext withPortBinders(List<PortBinder> portBinders) {
        this.portBinders = portBinders;
        return this;
    }

    public String getDataPath() {
        return dataPath;
    }

    public ConsulContext withDataPath(String dataPath) {
        this.dataPath = dataPath;
        return this;
    }

    public String getHostName() {
        return hostName;
    }

    public ConsulContext withHostName(String hostName) {
        this.hostName = hostName;
        return this;
    }

    public String[] getCommand() {
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

    public ConsulCommandBuilder getCommandBuilder() {
        return commandBuilder;
    }

    public ConsulContext withCommandBuilder(ConsulCommandBuilder commandBuilder) {
        Guard.toBeTrue(command == null || command.length == 0, "command already provided");
        this.commandBuilder = commandBuilder;
        return this;
    }
}
