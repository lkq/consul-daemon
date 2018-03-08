package com.kliu.services.docker.daemon.container.cmd;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerCmd;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.kliu.services.docker.daemon.TestConfigProvider;
import com.kliu.services.docker.daemon.config.Config;
import com.kliu.services.docker.daemon.IntegrationTest;
import com.kliu.services.docker.daemon.container.SimpleDockerClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import spark.utils.StringUtils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

class StartContainerTest {

    private StartContainer startContainer;
    @Mock
    private SimpleDockerClient simpleDockerClient;
    @Mock
    private DockerClient dockerClient;
    @Mock
    private StartContainerCmd startContainerCmd;
    @Mock
    private InspectContainerCmd inspectContainerCmd;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private InspectContainerResponse inspectContainerResponse;

    @BeforeEach
    void setUp() {
        initMocks(this);
        Config.init(new TestConfigProvider());
        startContainer = new StartContainer(simpleDockerClient);
    }

    @Test
    void canStartContainer() {
        given(simpleDockerClient.get()).willReturn(dockerClient);
        given(dockerClient.startContainerCmd(anyString())).willReturn(startContainerCmd);
        given(dockerClient.inspectContainerCmd(anyString())).willReturn(inspectContainerCmd);
        given(inspectContainerCmd.exec()).willReturn(inspectContainerResponse);
        given(inspectContainerResponse.getState().getRunning()).willReturn(true);

        Boolean started = startContainer.exec("dummy-container");

        assertThat(started, is(true));
        verify(startContainerCmd, times(1)).exec();
    }

    @IntegrationTest
    @Test
    void willActuallyStartContainer() {
        String containerID = null;
        try {
            simpleDockerClient = new SimpleDockerClient();
            String imageName = "hello-world";
            String containerName = imageName + "-" + System.currentTimeMillis();
            new PullImage(simpleDockerClient).exec(imageName, 0);
            containerID = new CreateContainer(dockerClient, imageName, containerName).exec();

            startContainer = new StartContainer(simpleDockerClient);

            startContainer.exec(containerName);
        } finally {
            if (StringUtils.isNotEmpty(containerID)) {
                simpleDockerClient.get().removeContainerCmd(containerID).withForce(true).exec();
            }
        }
    }
}