package com.kliu.services.docker.daemon.container.cmd;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.kliu.services.docker.daemon.IntegrationTest;
import com.kliu.services.docker.daemon.TestConfigProvider;
import com.kliu.services.docker.daemon.config.Config;
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
    private final String imageNameTag = "dummy-image";
    private final String containerName = "dummy-container";

    @BeforeEach
    void setUp() {
        initMocks(this);
        Config.init(new TestConfigProvider());
    }

    @Test
    void canCreateContainer() {


        given(simpleDockerClient.get()).willReturn(dockerClient);
        given(dockerClient.createContainerCmd(anyString())).willReturn(createContainerCmd);
        given(createContainerCmd.withName(anyString())).willReturn(createContainerCmd);
        given(createContainerCmd.exec()).willReturn(createContainerResponse);
        given(createContainerResponse.getId()).willReturn(containerName);

        createContainer = new CreateContainer(simpleDockerClient, imageNameTag, containerName);
        String containerID = createContainer.exec();

        assertThat(containerID, is(containerName));
    }

    @IntegrationTest
    @Test
    void canActuallyCreateContainer() {
        String containerID = null;
        try {
            simpleDockerClient = new SimpleDockerClient();

            String expectedContainerName = "hello-world-" + System.currentTimeMillis();
            createContainer = new CreateContainer(simpleDockerClient, "hello-world:latest", expectedContainerName);
            containerID = createContainer.exec();

            String containerName = simpleDockerClient.get().inspectContainerCmd(containerID).exec().getName();

            assertThat(containerName, is("/" + expectedContainerName));
        } finally {
            if (StringUtils.isNotEmpty(containerID)) {
                simpleDockerClient.get().removeContainerCmd(containerID).withForce(true).exec();
            }
        }
    }
}