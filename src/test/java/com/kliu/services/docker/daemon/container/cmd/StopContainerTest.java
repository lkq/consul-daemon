package com.kliu.services.docker.daemon.container.cmd;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.StopContainerCmd;
import com.kliu.services.docker.daemon.IntegrationTest;
import com.kliu.services.docker.daemon.config.ConfigProvider;
import com.kliu.services.docker.daemon.container.SimpleDockerClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

class StopContainerTest {
    private static Logger logger = LoggerFactory.getLogger(StopContainerTest.class);

    private StopContainer stopContainer;
    @Mock
    private SimpleDockerClient simpleDockerClient;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private DockerClient dockerClient;
    @Mock
    private StopContainerCmd stopContainerCmd;

    @BeforeEach
    void setUp() {
        initMocks(this);
        stopContainer = new StopContainer(simpleDockerClient);
    }

    @Test
    void canStopContainer() {
        given(simpleDockerClient.get()).willReturn(dockerClient);
        given(dockerClient.stopContainerCmd(anyString())).willReturn(stopContainerCmd);
        given(stopContainerCmd.withTimeout(anyInt())).willReturn(stopContainerCmd);

        stopContainer.exec("dummy-container");

        verify(stopContainerCmd, times(1)).exec();
    }

    @IntegrationTest
    @Test
    void willKeepSilenceIfContainerNotExists() {
        SimpleDockerClient simpleDockerClient = new SimpleDockerClient(ConfigProvider.NET_EASY_HUB);
        stopContainer = new StopContainer(simpleDockerClient);
        stopContainer.exec("dummy-container-" + System.currentTimeMillis());
    }

    @IntegrationTest
    @Test
    void willKeepSilenceIfContainerNotStarted() {
        String containerID = null;
        String imageName = "hello-world:latest";
        String containerName = "hello-world-" + System.currentTimeMillis();
        try {
            simpleDockerClient = new SimpleDockerClient(ConfigProvider.NET_EASY_HUB);
            PullImage pullImage = new PullImage(simpleDockerClient);
            CreateContainer createContainer = new CreateContainer(simpleDockerClient);
            stopContainer = new StopContainer(simpleDockerClient);
            pullImage.exec(imageName, 30);
            containerID = createContainer.exec(imageName, containerName);

            stopContainer.exec(containerName);
        } finally {
            if (StringUtils.isNotEmpty(containerID)) {
                simpleDockerClient.get().removeContainerCmd(containerID).withForce(true).exec();
            }
        }
    }

    @IntegrationTest
    @Test
    void canActuallyStopContainer() {
        String containerID = null;
        String imageName = "hello-world:latest";
        String containerName = "hello-world-" + System.currentTimeMillis();
        try {
            simpleDockerClient = new SimpleDockerClient(ConfigProvider.NET_EASY_HUB);
            PullImage pullImage = new PullImage(simpleDockerClient);
            CreateContainer createContainer = new CreateContainer(simpleDockerClient);
            StartContainer startContainer = new StartContainer(simpleDockerClient);
            stopContainer = new StopContainer(simpleDockerClient);
            pullImage.exec(imageName, 30);
            containerID = createContainer.exec(imageName, containerName);
            startContainer.exec(containerName);

            stopContainer.exec(containerName);
        } finally {
            if (StringUtils.isNotEmpty(containerID))
            simpleDockerClient.get().removeContainerCmd(containerID).withForce(true).exec();
        }
    }
}