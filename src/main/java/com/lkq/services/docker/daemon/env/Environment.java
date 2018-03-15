package com.lkq.services.docker.daemon.env;

import java.util.List;

public interface Environment {

    String ENV_CONSUL_ROLE = "consul.role";
    String ENV_CONSUL_CLUSTER_MEMBER = "consul.cluster.member";
    String ENV_NODE_NAME = "consul.nodeName";

    static Environment get() {
        return EnvironmentProvider.get();
    }




    enum ConsulRole {
        CLIENT,
        SERVER;
    }
    String nodeName();

    ConsulRole consulRole();
    List<String> clusterMembers();
    String getDataPath();
    String getNetwork();

}
