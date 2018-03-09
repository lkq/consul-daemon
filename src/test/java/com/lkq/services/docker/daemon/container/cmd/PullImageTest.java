package com.lkq.services.docker.daemon.container.cmd;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectImageCmd;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.lkq.services.docker.daemon.IntegrationTest;
import com.lkq.services.docker.daemon.TestConfigProvider;
import com.lkq.services.docker.daemon.config.Config;
import com.lkq.services.docker.daemon.container.SimpleDockerClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

class PullImageTest {

    private static Logger logger = LoggerFactory.getLogger(PullImageTest.class);

    private PullImage pullImage;
    @Mock
    private SimpleDockerClient simpleDockerClient;
    @Mock
    private DockerClient dockerClient;
    @Mock
    private PullImageCmd pullImageCmd;
    @Mock
    private PullImageResultCallback callback;
    @Mock
    private InspectImageCmd inspectImageCmd;
    @Mock
    private InspectImageResponse inspectImageResponse;

    @BeforeEach
    void setUp() {
        initMocks(this);
        Config.init(new TestConfigProvider());
        pullImage = new PullImage(simpleDockerClient);
    }

    @Test
    void canPullImage() {
        given(simpleDockerClient.get()).willReturn(dockerClient);
        given(dockerClient.pullImageCmd(anyString())).willReturn(pullImageCmd);
        given(pullImageCmd.exec(anyObject())).willReturn(callback);
        given(dockerClient.inspectImageCmd(anyString())).willReturn(inspectImageCmd);
        given(inspectImageCmd.exec()).willThrow(new RuntimeException("Mock Exception")).willReturn(inspectImageResponse);
        String imageName = "dummy-image";
        given(inspectImageResponse.getId()).willReturn(imageName);

        String imageID = pullImage.exec(imageName, 0);

        verify(pullImageCmd, times(1)).exec(anyObject());
        assertThat(imageID, is(imageName));
    }

    @Test
    void willSkipPullingIfImageAlreadyExists() {
        given(simpleDockerClient.get()).willReturn(dockerClient);
        given(dockerClient.inspectImageCmd("dummy-image")).willReturn(inspectImageCmd);
        given(inspectImageCmd.exec()).willReturn(inspectImageResponse);
        given(inspectImageResponse.getRepoTags()).willReturn(Arrays.asList("dummy-image"));
        pullImage.exec("dummy-image", 10);

        verify(dockerClient, never()).pullImageCmd(anyString());
    }

    @IntegrationTest
    @Test
    void canActuallyPullImage() {
        simpleDockerClient = new SimpleDockerClient();
        pullImage = new PullImage(simpleDockerClient);

        String imageName = "hello-world";

        pullImage.exec(imageName, 30);

        InspectImageResponse inspectResponse = simpleDockerClient.get().inspectImageCmd(imageName).exec();

        assertThat(inspectResponse.getRepoTags(), is(Arrays.asList("hello-world:latest")));
    }
}