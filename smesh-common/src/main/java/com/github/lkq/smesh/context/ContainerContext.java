package com.github.lkq.smesh.context;

import com.github.lkq.smesh.docker.CommandBuilder;
import com.github.lkq.smesh.docker.PortBinder;
import com.github.lkq.smesh.docker.VolumeBinder;

import java.util.List;

public class ContainerContext {

    private String imageName;
    private String nodeName;
    private String hostName;
    private String network;
    private List<String> environmentVariables;
    private List<PortBinder> portBinders;
    private List<VolumeBinder> volumeBinders;
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

    public List<PortBinder> portBinders() {
        return portBinders;
    }

    public ContainerContext portBinders(List<PortBinder> portBinders) {
        this.portBinders = portBinders;
        return this;
    }

    public List<VolumeBinder> volumeBinders() {
        return volumeBinders;
    }

    public ContainerContext volumeBinders(List<VolumeBinder> volumeBinders) {
        this.volumeBinders = volumeBinders;
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
                ", portBinders=" + portBinders +
                ", volumeBinders=" + volumeBinders +
                ", commands=" + commands +
                '}';
    }
}
