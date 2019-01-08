package com.github.lkq.smesh.linkerd.config;

import com.github.lkq.instadocker.docker.entity.PortBinding;
import com.github.lkq.instadocker.docker.entity.VolumeBinding;

import java.util.List;
import java.util.Map;

public class LinkerdContext {

    private String imageName;
    private String nodeName;
    private String hostName;
    private String network;
    private List<PortBinding> portBindings;
    private List<VolumeBinding> volumeBindings;
    private List<String> environmentVariables;
    private List<String> commands;
    private String configFilePath;
    private String configFileName;
    private String hostConfigFilePath;
    private Map<String, String> templateVariables;

    public String imageName() {
        return imageName;
    }

    public LinkerdContext imageName(String imageName) {
        this.imageName = imageName;
        return this;
    }

    public String nodeName() {
        return nodeName;
    }

    public LinkerdContext nodeName(String nodeName) {
        this.nodeName = nodeName;
        return this;
    }

    public String hostName() {
        return hostName;
    }

    public LinkerdContext hostName(String hostName) {
        this.hostName = hostName;
        return this;
    }

    public String network() {
        return network;
    }

    public LinkerdContext network(String network) {
        this.network = network;
        return this;
    }

    public List<PortBinding> portBindings() {
        return portBindings;
    }

    public LinkerdContext portBindings(List<PortBinding> portBindings) {
        this.portBindings = portBindings;
        return this;
    }

    public List<VolumeBinding> volumeBindings() {
        return volumeBindings;
    }

    public LinkerdContext volumeBindings(List<VolumeBinding> volumeBindings) {
        this.volumeBindings = volumeBindings;
        return this;
    }

    public List<String> environmentVariables() {
        return environmentVariables;
    }

    public LinkerdContext environmentVariables(List<String> environmentVariables) {
        this.environmentVariables = environmentVariables;
        return this;
    }

    public List<String> commands() {
        return commands;
    }

    public LinkerdContext commands(List<String> commands) {
        this.commands = commands;
        return this;
    }

    public String configFilePath() {
        return configFilePath;
    }

    public LinkerdContext configFilePath(String configFilePath) {
        this.configFilePath = configFilePath;
        return this;
    }

    public String configFileName() {
        return configFileName;
    }

    public LinkerdContext configFileName(String configFileName) {
        this.configFileName = configFileName;
        return this;
    }

    public String hostConfigFilePath() {
        return hostConfigFilePath;
    }

    public LinkerdContext hostConfigFilePath(String hostConfigFilePath) {
        this.hostConfigFilePath = hostConfigFilePath;
        return this;
    }

    public Map<String, String> templateVariables() {
        return templateVariables;
    }

    public LinkerdContext templateVariables(Map<String, String> templateVariables) {
        this.templateVariables = templateVariables;
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
                ", \"configFilePath\":\"" + configFilePath + "\"" +
                ", \"configFileName\":\"" + configFileName + "\"" +
                ", \"hostConfigFilePath\":\"" + hostConfigFilePath + "\"" +
                ", \"templateVariables\":" + templateVariables +
                '}';
    }
}
