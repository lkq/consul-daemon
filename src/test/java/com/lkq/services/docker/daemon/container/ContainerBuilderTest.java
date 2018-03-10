package com.lkq.services.docker.daemon.container;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.lkq.services.docker.daemon.IntegrationTest;
import com.lkq.services.docker.daemon.TestConfigProvider;
import com.lkq.services.docker.daemon.config.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.utils.StringUtils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;

class ContainerBuilderTest {

    private final String imageNameTag = "dummy-image";
    private final String containerName = "dummy-container";

    @BeforeEach
    void setUp() {
        initMocks(this);
        Config.init(new TestConfigProvider());
    }

    @Test
    void canCreateContainer() {
        SimpleDockerClient simpleDockerClient = mock(SimpleDockerClient.class);
        DockerClient dockerClient = mock(DockerClient.class);
        CreateContainerCmd createContainerCmd = mock(CreateContainerCmd.class);
        CreateContainerResponse createContainerResponse = mock(CreateContainerResponse.class);

        given(simpleDockerClient.get()).willReturn(dockerClient);
        given(dockerClient.createContainerCmd(anyString())).willReturn(createContainerCmd);
        given(createContainerCmd.withName(anyString())).willReturn(createContainerCmd);
        given(createContainerCmd.exec()).willReturn(createContainerResponse);
        given(createContainerResponse.getId()).willReturn(containerName);

        ContainerBuilder containerBuilder = new ContainerBuilder(dockerClient, imageNameTag, containerName);
        String containerID = containerBuilder.build();

        assertThat(containerID, is(containerName));
    }

    @IntegrationTest
    @Test
    void canActuallyCreateContainer() {
        String containerID = null;
        DockerClient client = DockerClientFactory.get();
        try {
            SimpleDockerClient simpleDockerClient = new SimpleDockerClient(client);

            String expectedContainerName = "hello-world-" + System.currentTimeMillis();
            ContainerBuilder containerBuilder = new ContainerBuilder(client, "hello-world:latest", expectedContainerName);
            containerID = containerBuilder.build();

            String containerName = simpleDockerClient.get().inspectContainerCmd(containerID).exec().getName();

            assertThat(containerName, is("/" + expectedContainerName));
        } finally {
            if (StringUtils.isNotEmpty(containerID)) {
                client.removeContainerCmd(containerID).withForce(true).exec();
            }
        }
    }
}