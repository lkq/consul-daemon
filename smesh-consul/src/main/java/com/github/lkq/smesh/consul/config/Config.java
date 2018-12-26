package com.github.lkq.smesh.consul.config;

public class Config {
    private ConsulContext consulContext;
    private boolean cleanStart = false;

    public ConsulContext consulContext() {
        return consulContext;
    }

    public Config consulContext(ConsulContext consulContext) {
        this.consulContext = consulContext;
        return this;
    }

    public boolean cleanStart() {
        return cleanStart;
    }

    public Config cleanStart(boolean cleanStart) {
        this.cleanStart = cleanStart;
        return this;
    }
}
