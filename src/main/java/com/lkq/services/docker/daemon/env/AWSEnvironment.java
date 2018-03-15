package com.lkq.services.docker.daemon.env;

import com.lkq.services.docker.daemon.env.aws.AWSClient;

import java.nio.file.Paths;
import java.util.List;

public class AWSEnvironment implements Environment {

    @Override
    public ConsulRole consulRole() {
        String tagValue = AWSClient.instance().getTagValue(ENV_CONSUL_ROLE, "");
        if ("server".equals(tagValue)) {
            return ConsulRole.SERVER;
        }
        return ConsulRole.CLIENT;
    }

    @Override
    public List<String> clusterMembers() {
        return AWSClient.instance().getInstanceIPByTagValue(ENV_CONSUL_ROLE, "server");
    }

    @Override
    public String getDataPath() {
        return Paths.get(".").toAbsolutePath().normalize().toString() + "/data";
    }

    @Override
    public String getNetwork() {
        return "host";
    }

    @Override
    public String nodeName() {
        return AWSClient.instance().getTagValue(ENV_NODE_NAME, "consul_node_" + System.currentTimeMillis());
    }
}
