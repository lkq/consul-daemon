package com.kliu.services.docker.daemon.container.cmd;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.kliu.services.docker.daemon.IntegrationTest;
import com.kliu.services.docker.daemon.config.ConfigProvider;
import com.kliu.services.docker.daemon.container.SimpleDockerClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import spark.utils.StringUtils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.MockitoAnnotations.initMocks;

class CreateContainerTest {

    private CreateContainer createContainer;
    @Mock
    private SimpleDockerClient simpleDockerClient;
    @Mock
    private DockerClient dockerClient;
    @Mock
    private CreateContainerCmd createContainerCmd;
    @Mock
    private CreateContainerResponse createContainerResponse;

    @BeforeEach
    void setUp() {
        initMocks(this);
        createContainer = new CreateContainer(simpleDockerClient);
    }

    @Test
    void canCreateContainer() {

        given(simpleDockerClient.get()).willReturn(dockerClient);
        given(dockerClient.createContainerCmd(anyString())).willReturn(createContainerCmd);
        given(createContainerCmd.withName(anyString())).willReturn(createContainerCmd);
        given(createContainerCmd.exec()).willReturn(createContainerResponse);
        String containerName = "dummy-container";
        given(createContainerResponse.getId()).willReturn(containerName);

        String containerID = createContainer.exec("dummy-image", containerName);

        assertThat(containerID, is(containerName));
    }

    @IntegrationTest
    @Test
    void canActuallyCreateContainer() {
        String containerID = null;
        try {
            simpleDockerClient = new SimpleDockerClient(ConfigProvider.NET_EASY_HUB);

            createContainer = new CreateContainer(simpleDockerClient);
            String expectedContainerName = "hello-world-" + System.currentTimeMillis();
            containerID = createContainer.exec("hello-world", expectedContainerName);

            String containerName = simpleDockerClient.get().inspectContainerCmd(containerID).exec().getName();

            assertThat(containerName, is("/" + expectedContainerName));
        } finally {
            if (StringUtils.isNotEmpty(containerID)) {
                simpleDockerClient.get().removeContainerCmd(containerID).withForce(true).exec();
            }
        }
    }
}