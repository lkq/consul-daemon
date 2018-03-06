package com.kliu.services.docker.daemon.container.cmd;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.StopContainerCmd;
import com.kliu.services.docker.daemon.IntegrationTest;
import com.kliu.services.docker.daemon.TestConfigProvider;
import com.kliu.services.docker.daemon.config.Config;
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
    private final String helloWorldImage = "hello-world:latest";

    @BeforeEach
    void setUp() {
        initMocks(this);
        Config.init(new TestConfigProvider());
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
        SimpleDockerClient simpleDockerClient = new SimpleDockerClient();
        stopContainer = new StopContainer(simpleDockerClient);
        stopContainer.exec("dummy-container-" + System.currentTimeMillis());
    }

    @IntegrationTest
    @Test
    void willKeepSilenceIfContainerNotStarted() {
        String containerID = null;
        String containerName = "hello-world-" + System.currentTimeMillis();
        try {
            simpleDockerClient = new SimpleDockerClient();
            PullImage pullImage = new PullImage(simpleDockerClient);
            CreateContainer createContainer = new CreateContainer(dockerClient, helloWorldImage, containerName);
            stopContainer = new StopContainer(simpleDockerClient);
            pullImage.exec(helloWorldImage, 30);
            containerID = createContainer.exec();

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
        String containerName = "hello-world-" + System.currentTimeMillis();
        try {
            simpleDockerClient = new SimpleDockerClient();
            PullImage pullImage = new PullImage(simpleDockerClient);
            CreateContainer createContainer = new CreateContainer(dockerClient, helloWorldImage, containerName);
            StartContainer startContainer = new StartContainer(simpleDockerClient);
            stopContainer = new StopContainer(simpleDockerClient);
            pullImage.exec(helloWorldImage, 30);
            containerID = createContainer.exec();
            startContainer.exec(containerName);

            stopContainer.exec(containerName);
        } finally {
            if (StringUtils.isNotEmpty(containerID))
            simpleDockerClient.get().removeContainerCmd(containerID).withForce(true).exec();
        }
    }
}