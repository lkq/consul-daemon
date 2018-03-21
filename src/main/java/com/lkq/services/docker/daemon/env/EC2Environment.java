package com.lkq.services.docker.daemon.env;

import com.lkq.services.docker.daemon.env.aws.EC2Client;

import java.util.List;

public class EC2Environment extends LinuxEnvironment {

    @Override
    public String nodeName() {
        return EC2Client.instance().getTagValue(ENV_NODE_NAME);
    }

    @Override
    public boolean isServer() {
        String tagValue = EC2Client.instance().getTagValue(ENV_CONSUL_ROLE, "");
        if ("server".equals(tagValue)) {
            return true;
        }
        return false;
    }

    @Override
    public List<String> clusterMembers() {
        return EC2Client.instance().getInstanceIPByTagValue(ENV_CONSUL_ROLE, "server");
    }

}
