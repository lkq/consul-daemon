package com.github.lkq.smesh.consul.env;

import com.github.lkq.smesh.consul.env.aws.EC2Factory;

import java.util.List;

public class EC2Environment extends LinuxEnvironment {

    @Override
    public String nodeName() {
        return EC2Factory.get().getTagValue(ENV_NODE_NAME);
    }

    @Override
    public boolean isServer() {
        String tagValue = EC2Factory.get().getTagValue(ENV_CONSUL_ROLE, "");
        if ("server".equals(tagValue)) {
            return true;
        }
        return false;
    }

    @Override
    public List<String> clusterMembers() {
        return EC2Factory.get().getInstanceIPByTagValue(ENV_CONSUL_ROLE, "server");
    }

}
