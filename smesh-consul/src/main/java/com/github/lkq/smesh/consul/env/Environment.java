package com.github.lkq.smesh.consul.env;

import java.util.List;

public interface Environment {

    String ENV_CONSUL_ROLE = "consul.role";
    String ENV_CONSUL_CLUSTER_MEMBER = "consul.cluster.member";
    String ENV_NODE_NAME = "consul.nodeName";

    static Environment get() {
        return EnvironmentProvider.get();
    }

    String nodeName();
    boolean isServer();
    List<String> clusterMembers();
    String consulDataPath();
    String network();
    Boolean forceRestart();
    String appVersion();
    int servicePort();
    int consulAPIPort();

}
