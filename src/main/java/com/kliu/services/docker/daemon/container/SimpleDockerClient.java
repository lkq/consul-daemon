package com.kliu.services.docker.daemon.container;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.kliu.services.docker.daemon.config.Config;

public class SimpleDockerClient {
    private DockerClient client;

    public SimpleDockerClient() {

        DefaultDockerClientConfig.Builder configBuilder = DefaultDockerClientConfig.createDefaultConfigBuilder();
        configBuilder.withRegistryUrl(Config.getRegistryURL());
        client = DockerClientBuilder.getInstance(configBuilder.build()).build();
    }

    public DockerClient get() {
        return client;
    }
}
