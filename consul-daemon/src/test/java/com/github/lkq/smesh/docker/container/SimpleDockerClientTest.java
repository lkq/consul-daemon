package com.github.lkq.smesh.docker.container;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.model.Frame;
import com.github.lkq.smesh.docker.ContainerLogger;
import com.github.lkq.smesh.docker.DockerClientFactory;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import com.lkq.services.docker.daemon.IntegrationTest;
import com.lkq.services.docker.daemon.exception.ConsulDaemonException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import spark.utils.StringUtils;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class SimpleDockerClientTest {

    private SimpleDockerClient simpleDockerClient;

    private final String helloWorldImage = "hello-world:latest";

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private DockerClient dockerClient;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private InspectContainerResponse inspectResponse;

    @BeforeEach
    void setUp() {
        initMocks(this);
        simpleDockerClient = SimpleDockerClient.create(dockerClient);
    }

    @Test
    void canStartContainer() {
        StartContainerCmd startContainerCmd = mock(StartContainerCmd.class);
        given(dockerClient.startContainerCmd(anyString())).willReturn(startContainerCmd);
        given(dockerClient.inspectContainerCmd(anyString()).exec()).willReturn(inspectResponse);
        given(inspectResponse.getState().getRunning()).willReturn(true);

        Boolean started = simpleDockerClient.startContainer("dummy-container");

        assertThat(started, is(true));
        verify(startContainerCmd, times(1)).exec();
    }

    @Test
    void willReturnTrueIfRenameSuccess() {
        boolean renamed = simpleDockerClient.renameContainer("dummy-container", "new-name");

        assertTrue(renamed);
    }

    @Test
    void willReturnFalseIfExceptionHappens() {
        given(dockerClient.renameContainerCmd(anyString())).willThrow(new ConsulDaemonException("mock exception"));
        boolean renamed = simpleDockerClient.renameContainer("dummy-container", "new-name");

        assertFalse(renamed);
    }

    @IntegrationTest
    @Test
    void testContainerLifeCycle() {
        String containerID = null;
        SimpleDockerClient client = SimpleDockerClient.create(DockerClientFactory.get());
        try {
            String imageName = "hello-world";
            String containerName = imageName + "-" + System.currentTimeMillis();
            client.pullImage(imageName);
            containerID = client.createContainer(imageName, containerName).build();
            Boolean started = client.startContainer(containerID);
            assertTrue(started);
        } finally {
            if (StringUtils.isNotEmpty(containerID)) {
                boolean removed = client.removeContainer(containerID);
                assertTrue(removed);
            }
        }
    }

    @IntegrationTest
    @Test
    void willReturnFalseIfContainerNotExists() {
        String currentName = "dummy-container-" + System.currentTimeMillis();
        SimpleDockerClient client = SimpleDockerClient.create(DockerClientFactory.get());
        boolean renamed = client.renameContainer(currentName, "dummy-container");
        assertThat(renamed, is(false));
    }

    @IntegrationTest
    @Test
    void canRenameContainer() throws InterruptedException, IOException {
        String containerID = null;
        SimpleDockerClient client = SimpleDockerClient.create(DockerClientFactory.get());
        try {
            client.pullImage(helloWorldImage);
            String oldContainerName = "hello-world-" + System.currentTimeMillis();
            String newContainerName = oldContainerName + "-1";
            containerID = client.createContainer(helloWorldImage, oldContainerName).build();
            boolean renamed = client.renameContainer(oldContainerName, newContainerName);

            assertThat(renamed, is(true));

            InspectContainerResponse inspectResponse = client.get().inspectContainerCmd(containerID).exec();
            assertThat(inspectResponse.getName(), is("/" + newContainerName));

        } finally {
            if (StringUtils.isNotEmpty(containerID)) {
                client.removeContainer(containerID);
            }
        }
    }

    @IntegrationTest
    @Test
    void canAttachContainerLogs() {
        String containerName = "hello-world-" + System.currentTimeMillis();
        SimpleDockerClient client = SimpleDockerClient.create(DockerClientFactory.get());
        assertTrue(client.pullImage("busybox:latest"));

        String containerID = null;
        try {
            containerID = client.createContainer(helloWorldImage, containerName).build();
            ContainerLogger containerLogger = mock(ContainerLogger.class);
            String finalContainerID = containerID;
            new Thread(() -> client.startContainer(finalContainerID)).run();
            client.attachLogging(containerID, containerLogger);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            verify(containerLogger, atLeast(1)).onNext(any(Frame.class));
        }finally {
            if (StringUtils.isNotEmpty(containerID)) {
                client.removeContainer(containerID);
            }
        }
    }
}