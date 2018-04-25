package com.github.lkq.smesh.docker.container;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.lkq.smesh.StringUtils;
import com.github.lkq.smesh.docker.ContainerBuilder;
import com.github.lkq.smesh.docker.DockerClientFactory;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.BDDMockito.given;

class ContainerBuilderTest {

    private final String imageNameTag = "dummy-image";
    private final String containerName = "dummy-container";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void canCreateContainer() {
        SimpleDockerClient simpleDockerClient = Mockito.mock(SimpleDockerClient.class);
        DockerClient dockerClient = Mockito.mock(DockerClient.class);
        CreateContainerCmd createContainerCmd = Mockito.mock(CreateContainerCmd.class);
        CreateContainerResponse createContainerResponse = Mockito.mock(CreateContainerResponse.class);

        given(simpleDockerClient.get()).willReturn(dockerClient);
        given(dockerClient.createContainerCmd(Matchers.anyString())).willReturn(createContainerCmd);
        given(createContainerCmd.withName(Matchers.anyString())).willReturn(createContainerCmd);
        given(createContainerCmd.exec()).willReturn(createContainerResponse);
        given(createContainerResponse.getId()).willReturn(containerName);

        ContainerBuilder containerBuilder = new ContainerBuilder(dockerClient, imageNameTag, containerName);
        String containerID = containerBuilder.build();

        MatcherAssert.assertThat(containerID, CoreMatchers.is(containerName));
    }

    @IntegrationTest
    @Test
    void canActuallyCreateContainer() {
        String containerID = null;
        DockerClient client = DockerClientFactory.get();
        try {
            SimpleDockerClient simpleDockerClient = SimpleDockerClient.create(client);

            String expectedContainerName = "hello-world-" + System.currentTimeMillis();
            ContainerBuilder containerBuilder = new ContainerBuilder(client, "hello-world:latest", expectedContainerName);
            containerID = containerBuilder.build();

            String containerName = simpleDockerClient.get().inspectContainerCmd(containerID).exec().getName();

            MatcherAssert.assertThat(containerName, CoreMatchers.is("/" + expectedContainerName));
        } finally {
            if (StringUtils.isNotEmpty(containerID)) {
                client.removeContainerCmd(containerID).withForce(true).exec();
            }
        }
    }
}