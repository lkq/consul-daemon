package com.github.lkq.smesh.consul.config;

public class Config {
    private String imageName;
    private String containerName;

    public String imageName() {
        return imageName;
    }

    public Config imageName(String imageName) {
        this.imageName = imageName;
        return this;
    }

    public String containerName() {
        return containerName;
    }

    public Config containerName(String containerName) {
        this.containerName = containerName;
        return this;
    }
}
