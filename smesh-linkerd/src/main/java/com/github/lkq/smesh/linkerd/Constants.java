package com.github.lkq.smesh.linkerd;

public interface Constants {
    String LINKERD_CONFIG_PREFIX = "smesh-linkerd";
    String CONTAINER_CONFIG_PATH = "/linkerd";
    String LINKERD_IMAGE = "buoyantio/linkerd:1.3.7";
    String CONFIG_FINENAME = "smesh-linkerd.yaml";
    String VAR_CONSUL_HOST = "consulHost";
}
