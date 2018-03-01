package com.kliu.services.docker.daemon.container.cmd;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerCmd;
import com.github.dockerjava.api.command.InspectContainerResponse;
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

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

class RenameContainerCmdTest {
    private static Logger logger = LoggerFactory.getLogger(RenameContainerCmdTest.class);
    private final String helloWorldImage = "hello-world:latest";

    private RenameContainer renameContainer;
    @Mock
    private SimpleDockerClient simpleDockerClient;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private DockerClient dockerClient;
    @Mock
    private InspectContainerCmd inspectContainerCmd;
    @Mock
    private com.github.dockerjava.api.command.RenameContainerCmd renameContainerCmdCmd;
    @Mock
    private InspectContainerResponse inspectResponse;

    @BeforeEach
    void setUp() {
        initMocks(this);
        renameContainer = new RenameContainer(simpleDockerClient);
    }

    @Test
    void canRenameContainer() {
        given(simpleDockerClient.get()).willReturn(dockerClient);
        given(dockerClient.inspectContainerCmd(anyString())).willReturn(inspectContainerCmd);
        given(dockerClient.renameContainerCmd(anyString()).withName(anyString())).willReturn(renameContainerCmdCmd);
        given(inspectContainerCmd.exec()).willReturn(inspectResponse);

        renameContainer.exec("dummy-container", "new-name");

        verify(renameContainerCmdCmd, times(1)).exec();
    }

    @IntegrationTest
    @Test
    void willSilenceIfContainerNotExists() {
        renameContainer = new RenameContainer(new SimpleDockerClient(null));
        String currentName = "dummy-container-" + System.currentTimeMillis();
        boolean renamed = renameContainer.exec(currentName, "dummy-container");
        assertThat(renamed, is(false));
    }

    @IntegrationTest
    @Test
    void canActuallyRenameContainer() throws InterruptedException, IOException {
        String containerID = null;
        try {
            simpleDockerClient = new SimpleDockerClient(ConfigProvider.NET_EASY_HUB);
            new PullImage(simpleDockerClient).exec(helloWorldImage, 0);
            String oldContainerName = "hello-world-" + System.currentTimeMillis();
            String newContainerID = oldContainerName + "-1";
            containerID = new CreateContainer(simpleDockerClient, helloWorldImage, oldContainerName).exec();
            boolean renamed = new RenameContainer(simpleDockerClient).exec(oldContainerName, newContainerID);
            assertThat(renamed, is(true));

            InspectContainerResponse inspectResponse = simpleDockerClient.get().inspectContainerCmd(containerID).exec();
            assertThat(inspectResponse.getName(), is("/" + newContainerID));

        } finally {
            if (StringUtils.isNotEmpty(containerID)) {
                simpleDockerClient.get().removeContainerCmd(containerID).exec();
            }
        }
    }
}