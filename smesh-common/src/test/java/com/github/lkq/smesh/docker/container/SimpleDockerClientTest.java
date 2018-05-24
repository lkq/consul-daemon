package com.github.lkq.smesh.docker.container;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.model.Frame;
import com.github.lkq.smesh.docker.ContainerLogger;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import com.github.lkq.smesh.exception.SmeshException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import spark.utils.StringUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
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
        StartContainerCmd startContainerCmd = Mockito.mock(StartContainerCmd.class);
        given(dockerClient.startContainerCmd(anyString())).willReturn(startContainerCmd);
        given(dockerClient.inspectContainerCmd(anyString()).exec()).willReturn(inspectResponse);
        given(inspectResponse.getState().getRunning()).willReturn(true);

        Boolean started = simpleDockerClient.startContainer("dummy-container");

        MatcherAssert.assertThat(started, CoreMatchers.is(true));
        verify(startContainerCmd, Mockito.times(1)).exec();
    }

    @Test
    void willReturnTrueIfRenameSuccess() {
        boolean renamed = simpleDockerClient.renameContainer("dummy-container", "new-name");

        assertTrue(renamed);
    }

    @Test
    void willReturnFalseIfExceptionHappens() {
        given(dockerClient.renameContainerCmd(anyString())).willThrow(new SmeshException("mock exception"));
        boolean renamed = simpleDockerClient.renameContainer("dummy-container", "new-name");

        Assertions.assertFalse(renamed);
    }

    @IntegrationTest
    @Test
    void testContainerLifeCycle() {
        String containerID = null;
        SimpleDockerClient client = SimpleDockerClient.create();
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
        SimpleDockerClient client = SimpleDockerClient.create();
        boolean renamed = client.renameContainer(currentName, "dummy-container");
        MatcherAssert.assertThat(renamed, CoreMatchers.is(false));
    }

    @IntegrationTest
    @Test
    void canRenameContainer() throws InterruptedException, IOException {
        String containerID = null;
        SimpleDockerClient client = SimpleDockerClient.create();
        try {
            client.pullImage(helloWorldImage);
            String oldContainerName = "hello-world-" + System.currentTimeMillis();
            String newContainerName = oldContainerName + "-1";
            containerID = client.createContainer(helloWorldImage, oldContainerName).build();
            boolean renamed = client.renameContainer(oldContainerName, newContainerName);

            MatcherAssert.assertThat(renamed, CoreMatchers.is(true));

            InspectContainerResponse inspectResponse = client.get().inspectContainerCmd(containerID).exec();
            MatcherAssert.assertThat(inspectResponse.getName(), CoreMatchers.is("/" + newContainerName));

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
        SimpleDockerClient client = SimpleDockerClient.create();
        assertTrue(client.pullImage("busybox:latest"));

        String containerID = null;
        try {
            containerID = client.createContainer(helloWorldImage, containerName).build();
            ContainerLogger containerLogger = Mockito.mock(ContainerLogger.class);
            String finalContainerID = containerID;
            new Thread(() -> client.startContainer(finalContainerID)).run();
            client.attachLogging(containerID, containerLogger);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            verify(containerLogger, Mockito.atLeast(1)).onNext(Matchers.any(Frame.class));
        }finally {
            if (StringUtils.isNotEmpty(containerID)) {
                client.removeContainer(containerID);
            }
        }
    }
}