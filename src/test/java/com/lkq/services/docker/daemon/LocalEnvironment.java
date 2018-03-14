package com.lkq.services.docker.daemon;

import com.lkq.services.docker.daemon.env.Environment;

import java.util.Collections;
import java.util.List;

public class LocalEnvironment implements Environment {
    @Override
    public ConsulRole consulRole() {
        return ConsulRole.SERVER;
    }

    @Override
    public List<String> clusterMembers() {
        return Collections.emptyList();
    }

    @Override
    public String getDataPath() {
        return "";
    }
}
