package com.lkq.services.docker.daemon.env;

import com.lkq.services.docker.daemon.env.aws.AWSClient;

import java.util.List;

public class AWSEnvironment extends LinuxEnvironment {

    @Override
    public String nodeName() {
        return AWSClient.instance().getTagValue(ENV_NODE_NAME);
    }

    @Override
    public boolean isServer() {
        String tagValue = AWSClient.instance().getTagValue(ENV_CONSUL_ROLE, "");
        if ("server".equals(tagValue)) {
            return true;
        }
        return false;
    }

    @Override
    public List<String> clusterMembers() {
        return AWSClient.instance().getInstanceIPByTagValue(ENV_CONSUL_ROLE, "server");
    }

}
