package com.github.lkq.smesh.consul.config;

public class Config {
    private ConsulContext consulContext;
    private String imageName;
    private String containerName;

    public ConsulContext consulContext() {
        return consulContext;
    }

    public Config consulContext(ConsulContext consulContext) {
        this.consulContext = consulContext;
        return this;
    }
}
