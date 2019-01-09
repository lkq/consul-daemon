package com.github.lkq.smesh.consul;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;

public class DockerClientFactory {

    private static DockerClient _instance;

    public static synchronized DockerClient create() {
        if (_instance == null) {
            DefaultDockerClientConfig.Builder configBuilder = DefaultDockerClientConfig.createDefaultConfigBuilder();
            _instance = DockerClientBuilder.getInstance(configBuilder.build()).build();
        }
        return _instance;
    }
}
