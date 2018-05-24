package com.github.lkq.smesh.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;

public class DockerClientFactory {

    private static DockerClient client;

    public synchronized static DockerClient get() {
        if (client == null) {
            client = create();
        }
        return client;
    }

    public static DockerClient create() {
        DefaultDockerClientConfig.Builder configBuilder = DefaultDockerClientConfig.createDefaultConfigBuilder();
        return DockerClientBuilder.getInstance(configBuilder.build()).build();
    }
}
