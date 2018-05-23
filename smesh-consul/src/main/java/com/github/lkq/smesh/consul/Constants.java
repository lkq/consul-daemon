package com.github.lkq.smesh.consul;

public interface Constants {

    String NET_EASY_HUB = "http://hub-mirror.c.163.com";
    String CONTAINER_DATA_PATH = "/consul/data";
    String APP_NAME = "smesh-consul";
    String CONSUL_IMAGE = "consul:1.0.6";
    String CONSUL_URL = "http://localhost:8500";
    String ENV_NODE_NAME = "consul.node.name";
    String ENV_CONSUL_ROLE = "consul.node.role";
    String ENV_CONSUL_CLUSTER_MEMBERS = "consul.cluster.members";
}
