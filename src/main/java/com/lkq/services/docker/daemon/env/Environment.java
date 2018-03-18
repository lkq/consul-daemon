package com.lkq.services.docker.daemon.env;

import java.util.List;

public interface Environment {

    String ENV_CONSUL_ROLE = "consul.role";
    String ENV_CONSUL_CLUSTER_MEMBER = "consul.cluster.member";
    String ENV_NODE_NAME = "consul.nodeName";

    enum ConsulRole {
        CLIENT,
        SERVER

    }

    static Environment get() {
        return EnvironmentProvider.get();
    }

    String nodeName();
    ConsulRole consulRole();
    List<String> clusterMembers();
    String dataPath();
    String network();
    Boolean forceRestart();
    String appVersion();
    int servicePort();
    int consulAPIPort();

}
