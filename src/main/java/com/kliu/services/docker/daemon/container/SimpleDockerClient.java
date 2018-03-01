package com.kliu.services.docker.daemon.container;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import spark.utils.StringUtils;

public class SimpleDockerClient {
    private DockerClient client;

    public SimpleDockerClient(String registryURL) {

        DefaultDockerClientConfig.Builder configBuilder = DefaultDockerClientConfig.createDefaultConfigBuilder();
        if (StringUtils.isNotEmpty(registryURL)) {
            configBuilder.withRegistryUrl(registryURL);
        }
        client = DockerClientBuilder.getInstance(configBuilder.build()).build();
    }

    public DockerClient get() {
        return client;
    }
}
