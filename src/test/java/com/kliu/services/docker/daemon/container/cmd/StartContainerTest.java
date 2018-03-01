package com.kliu.services.docker.daemon.container.cmd;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.kliu.services.docker.daemon.config.ConfigProvider;
import com.kliu.services.docker.daemon.container.SimpleDockerClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import spark.utils.StringUtils;

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

    @BeforeEach
    void setUp() {
        initMocks(this);
        startContainer = new StartContainer(simpleDockerClient);
    }

    @Test
    void canStartContainer() {
        given(simpleDockerClient.get()).willReturn(dockerClient);
        given(dockerClient.startContainerCmd(anyString())).willReturn(startContainerCmd);
        startContainer.exec("dummy-container");

        verify(startContainerCmd, times(1)).exec();
    }

    @Test
    void willActuallyStartContainer() {
        String containerID = null;
        try {
            simpleDockerClient = new SimpleDockerClient(ConfigProvider.NET_EASY_HUB);
            String imageName = "hello-world";
            String containerName = imageName + "-" + System.currentTimeMillis();
            new PullImage(simpleDockerClient).exec(imageName, 0);
            containerID = new CreateContainer(simpleDockerClient).exec(imageName, containerName);

            startContainer = new StartContainer(simpleDockerClient);

            startContainer.exec(containerName);
        } finally {
            if (StringUtils.isNotEmpty(containerID)) {
                simpleDockerClient.get().removeContainerCmd(containerID).withForce(true).exec();
            }
        }
    }
}