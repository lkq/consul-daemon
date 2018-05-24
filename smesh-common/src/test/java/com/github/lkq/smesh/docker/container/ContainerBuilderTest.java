package com.github.lkq.smesh.docker.container;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.InternetProtocol;
import com.github.dockerjava.api.model.Ports;
import com.github.lkq.smesh.context.PortBinding;
import com.github.lkq.smesh.context.VolumeBinding;
import com.github.lkq.smesh.docker.ContainerBuilder;
import com.github.lkq.smesh.docker.DockerClientFactory;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import spark.utils.StringUtils;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

class ContainerBuilderTest {

    private final String imageNameTag = "dummy-image";
    private final String containerName = "dummy-container";

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private DockerClient dockerClient;
    @Mock
    private CreateContainerCmd createContainerCmd;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void canCreateContainer() {
        CreateContainerResponse createContainerResponse = Mockito.mock(CreateContainerResponse.class);

        given(dockerClient.createContainerCmd(Matchers.anyString())).willReturn(createContainerCmd);
        given(createContainerCmd.withName(Matchers.anyString())).willReturn(createContainerCmd);
        given(createContainerCmd.exec()).willReturn(createContainerResponse);
        given(createContainerResponse.getId()).willReturn(containerName);

        ContainerBuilder containerBuilder = new ContainerBuilder(dockerClient, imageNameTag, containerName);
        String containerID = containerBuilder.build();

        assertThat(containerID, is(containerName));
    }

    @Test
    void canSetupVolume() {
        given(dockerClient.createContainerCmd(imageNameTag).withName(containerName)).willReturn(createContainerCmd);

        ContainerBuilder builder = new ContainerBuilder(dockerClient, imageNameTag, containerName);

        builder.withVolume(Arrays.asList(new VolumeBinding("/opt/data", "/data"), new VolumeBinding("/etc/config", "/config")));

        ArgumentCaptor<List> volumeCaptor = ArgumentCaptor.forClass(List.class);
        verify(createContainerCmd, times(1)).withBinds(volumeCaptor.capture());

        List<Bind> binds = volumeCaptor.getValue();

        assertThat(binds.size(), is(2));
        assertThat(binds.get(0).getPath(), is("/opt/data"));
        assertThat(binds.get(0).getVolume().getPath(), is("/data"));
        assertThat(binds.get(1).getPath(), is("/etc/config"));
        assertThat(binds.get(1).getVolume().getPath(), is("/config"));
    }

    @Test
    void canSetupPorts() {
        given(dockerClient.createContainerCmd(imageNameTag).withName(containerName)).willReturn(createContainerCmd);
        given(createContainerCmd.withExposedPorts(anyListOf(ExposedPort.class))).willReturn(createContainerCmd);
        given(createContainerCmd.withPortBindings(anyListOf(com.github.dockerjava.api.model.PortBinding.class))).willReturn(createContainerCmd);

        ContainerBuilder builder = new ContainerBuilder(dockerClient, imageNameTag, containerName);

        builder.withPortBinders(Arrays.asList(new PortBinding(1000, com.github.lkq.smesh.context.PortBinding.Protocol.TCP), new com.github.lkq.smesh.context.PortBinding(2000, com.github.lkq.smesh.context.PortBinding.Protocol.UDP)));

        ArgumentCaptor<List> portCaptor = ArgumentCaptor.forClass(List.class);
        verify(createContainerCmd, times(1)).withExposedPorts(portCaptor.capture());

        List<ExposedPort> binds = portCaptor.getValue();
        assertThat(binds.size(), is(2));
        assertThat(binds.get(0).getPort(), is(1000));
        assertThat(binds.get(0).getProtocol(), is(InternetProtocol.TCP));
        assertThat(binds.get(1).getPort(), is(2000));
        assertThat(binds.get(1).getProtocol(), is(InternetProtocol.UDP));

        ArgumentCaptor<Ports> portsCaptor = ArgumentCaptor.forClass(Ports.class);
        verify(createContainerCmd, times(1)).withPortBindings(portsCaptor.capture());

        Ports ports = portsCaptor.getValue();
        assertThat(ports.getBindings().size(), is(2));
        Ports.Binding bindings1 = ports.getBindings().get(binds.get(0))[0];
        assertThat(bindings1.getHostPortSpec(), is("1000"));
        Ports.Binding bindings2 = ports.getBindings().get(binds.get(1))[0];
        assertThat(bindings2.getHostPortSpec(), is("2000"));

    }

    @IntegrationTest
    @Test
    void canActuallyCreateContainer() {
        String containerID = null;
        DockerClient client = DockerClientFactory.create();
        try {
            SimpleDockerClient simpleDockerClient = SimpleDockerClient.create(client);

            String expectedContainerName = "hello-world-" + System.currentTimeMillis();
            ContainerBuilder containerBuilder = new ContainerBuilder(client, "hello-world:latest", expectedContainerName);
            containerID = containerBuilder.build();

            String containerName = simpleDockerClient.get().inspectContainerCmd(containerID).exec().getName();

            assertThat(containerName, is("/" + expectedContainerName));
        } finally {
            if (StringUtils.isNotEmpty(containerID)) {
                client.removeContainerCmd(containerID).withForce(true).exec();
            }
        }
    }
}