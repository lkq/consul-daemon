package com.github.lkq.smesh.docker.container;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import com.github.lkq.smesh.StringUtils;
import com.github.lkq.smesh.docker.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

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

        builder.withVolume(Arrays.asList(new VolumeBinder("/opt/data", "/data"), new VolumeBinder("/etc/config", "/config")));

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
        given(createContainerCmd.withPortBindings(anyListOf(PortBinding.class))).willReturn(createContainerCmd);

        ContainerBuilder builder = new ContainerBuilder(dockerClient, imageNameTag, containerName);

        builder.withPortBinders(Arrays.asList(new PortBinder(1000, PortBinder.Protocol.TCP), new PortBinder(2000, PortBinder.Protocol.UDP)));

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
        DockerClient client = DockerClientFactory.get();
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