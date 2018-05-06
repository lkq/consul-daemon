package com.github.lkq.smesh.context;

import java.util.List;

public class ContainerContext {

    private String imageName;
    private String nodeName;
    private String hostName;
    private String network;
    private List<String> environmentVariables;
    private List<PortBinding> portBindings;
    private List<VolumeBinding> volumeBindings;
    private CommandBuilder commands;

    public String imageName() {
        return imageName;
    }

    public ContainerContext imageName(String imageName) {
        this.imageName = imageName;
        return this;
    }

    public String nodeName() {
        return nodeName;
    }

    public ContainerContext nodeName(String nodeName) {
        this.nodeName = nodeName;
        return this;
    }

    public String network() {
        return network;
    }

    public ContainerContext network(String network) {
        this.network = network;
        return this;
    }

    public List<String> environmentVariables() {
        return environmentVariables;
    }

    public ContainerContext environmentVariables(List<String> environmentVariables) {
        this.environmentVariables = environmentVariables;
        return this;
    }

    public List<PortBinding> portBindings() {
        return portBindings;
    }

    public ContainerContext portBindings(List<PortBinding> portBindings) {
        this.portBindings = portBindings;
        return this;
    }

    public List<VolumeBinding> volumeBindings() {
        return volumeBindings;
    }

    public ContainerContext volumeBindings(List<VolumeBinding> volumeBindings) {
        this.volumeBindings = volumeBindings;
        return this;
    }

    public String hostName() {
        return hostName;
    }

    public ContainerContext hostName(String hostName) {
        this.hostName = hostName;
        return this;
    }

    public CommandBuilder commandBuilder() {
        return commands;
    }

    public ContainerContext commandBuilder(CommandBuilder commandBuilder) {
        this.commands = commandBuilder;
        return this;
    }

    @Override
    public String toString() {
        return "ContainerContext{" +
                "imageName='" + imageName + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", hostName='" + hostName + '\'' +
                ", network='" + network + '\'' +
                ", environmentVariables=" + environmentVariables +
                ", portBindings=" + portBindings +
                ", volumeBindings=" + volumeBindings +
                ", commands=" + commands +
                '}';
    }
}
