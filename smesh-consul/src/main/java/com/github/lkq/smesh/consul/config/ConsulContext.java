package com.github.lkq.smesh.consul.config;

import com.github.lkq.instadocker.docker.entity.PortBinding;
import com.github.lkq.instadocker.docker.entity.VolumeBinding;

import java.util.List;

public class ConsulContext {

    private String imageName;
    private String nodeName;
    private String hostName;
    private String network;
    private List<PortBinding> portBindings;
    private List<VolumeBinding> volumeBindings;
    private List<String> environmentVariables;
    private List<String> commands;

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

    public String hostName() {
        return hostName;
    }

    public ConsulContext hostName(String hostName) {
        this.hostName = hostName;
        return this;
    }

    public String network() {
        return network;
    }

    public ConsulContext network(String network) {
        this.network = network;
        return this;
    }

    public List<PortBinding> portBindings() {
        return portBindings;
    }

    public ConsulContext portBindings(List<PortBinding> portBindings) {
        this.portBindings = portBindings;
        return this;
    }

    public List<VolumeBinding> volumeBindings() {
        return volumeBindings;
    }

    public ConsulContext volumeBindings(List<VolumeBinding> volumeBindings) {
        this.volumeBindings = volumeBindings;
        return this;
    }

    public List<String> environmentVariables() {
        return environmentVariables;
    }

    public ConsulContext environmentVariables(List<String> environmentVariables) {
        this.environmentVariables = environmentVariables;
        return this;
    }

    public List<String> commands() {
        return commands;
    }

    public ConsulContext commands(List<String> commands) {
        this.commands = commands;
        return this;
    }

    @Override
    public String toString() {
        return "{" +
                "\"imageName\":\"" + imageName + "\"" +
                ", \"nodeName\":\"" + nodeName + "\"" +
                ", \"hostName\":\"" + hostName + "\"" +
                ", \"network\":\"" + network + "\"" +
                ", \"portBindings\":" + portBindings +
                ", \"volumeBindings\":" + volumeBindings +
                ", \"environmentVariables\":" + environmentVariables +
                ", \"commands\":" + commands +
                '}';
    }
}
