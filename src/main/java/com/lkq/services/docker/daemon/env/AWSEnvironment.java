package com.lkq.services.docker.daemon.env;

import com.lkq.services.docker.daemon.env.aws.AWSClient;

import java.nio.file.Paths;
import java.util.List;

public class AWSEnvironment implements Environment {
    @Override
    public ConsulRole consulRole() {
        String tagValue = AWSClient.instance().getTagValue("consul.role", "");
        if ("server".equals(tagValue)) {
            return ConsulRole.SERVER;
        }
        return ConsulRole.CLIENT;
    }

    @Override
    public List<String> clusterMembers() {
        return AWSClient.instance().getInstanceIPByTagValue("consul.role", "server");
    }

    @Override
    public String getDataPath() {
        return Paths.get(".").toAbsolutePath().normalize().toString() + "/data";
    }
}
