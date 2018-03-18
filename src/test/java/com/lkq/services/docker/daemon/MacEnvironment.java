package com.lkq.services.docker.daemon;

import com.lkq.services.docker.daemon.env.LinuxEnvironment;

import java.util.Collections;
import java.util.List;

public class MacEnvironment extends LinuxEnvironment {
    @Override
    public String nodeName() {
        return "consul";
    }

    @Override
    public ConsulRole consulRole() {
        return ConsulRole.SERVER;
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
        return false;
    }

    @Override
    public String jarVersion() {
        return "1.2.3";
    }
}
