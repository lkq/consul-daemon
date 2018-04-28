package com.github.lkq.smesh.linkerd.context;

import com.github.lkq.smesh.docker.CommandBuilder;
import com.github.lkq.smesh.docker.PortBinder;

import java.util.List;

public class LinkerdContext {

    public String imageName() {
        return "buoyantio/linkerd:1.3.7";
    }

    public String containerName() {
        return "linkerd";
    }

    public String network() {
        return "";
    }

    public List<PortBinder> portBinders() {
        return null;
    }

    public CommandBuilder commandBuilder() {
        return new LinkerdCommandBuilder();
    }
}
