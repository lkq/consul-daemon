package com.github.lkq.smesh.linkerd.config;

public class Config {
    private LinkerdContext linkerdContext;
    private boolean cleanStart = false;
    private String localLinkerdConfigPath;

    public LinkerdContext linkerdContext() {
        return linkerdContext;
    }

    public Config linkerdContext(LinkerdContext linkerdContext) {
        this.linkerdContext = linkerdContext;
        return this;
    }

    public boolean cleanStart() {
        return cleanStart;
    }

    public Config cleanStart(boolean cleanStart) {
        this.cleanStart = cleanStart;
        return this;
    }

    public Config localLinkerdConfigPath(String localLinkerdConfigPath) {
        this.localLinkerdConfigPath = localLinkerdConfigPath;
        return this;
    }
}
