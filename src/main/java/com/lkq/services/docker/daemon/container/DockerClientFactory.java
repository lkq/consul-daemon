package com.lkq.services.docker.daemon.container;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;

public class DockerClientFactory {

    private static DockerClient client;

    public synchronized static DockerClient get() {
        if (client == null) {
            DefaultDockerClientConfig.Builder configBuilder = DefaultDockerClientConfig.createDefaultConfigBuilder();
            client = DockerClientBuilder.getInstance(configBuilder.build()).build();
        }
        return client;
    }
}
