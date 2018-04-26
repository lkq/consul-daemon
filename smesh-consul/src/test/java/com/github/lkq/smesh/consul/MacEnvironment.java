package com.github.lkq.smesh.consul;

import com.github.lkq.smesh.consul.env.LinuxEnvironment;

import java.util.Collections;
import java.util.List;

public class MacEnvironment extends LinuxEnvironment {
    @Override
    public String nodeName() {
        return "consul";
    }

    @Override
    public boolean isServer() {
        return true;
    }

    @Override
    public List<String> clusterMembers() {
        return Collections.emptyList();
    }

    @Override
    public String dataPath() {
        return "";
    }

    @Override
    public String network() {
        return "";
    }

    @Override
    public Boolean forceRestart() {
        return null;
    }

    @Override
    public String appVersion() {
        return "1.2.3";
    }
}
