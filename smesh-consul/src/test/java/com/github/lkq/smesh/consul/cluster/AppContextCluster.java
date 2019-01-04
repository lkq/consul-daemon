package com.github.lkq.smesh.consul.cluster;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.lkq.instadocker.docker.entity.PortBinding;
import com.github.lkq.instadocker.docker.entity.VolumeBinding;
import com.github.lkq.smesh.consul.Constants;
import com.github.lkq.smesh.consul.app.AppContext;
import com.github.lkq.smesh.consul.client.ConsulClient;
import com.github.lkq.smesh.consul.client.ResponseParser;
import com.github.lkq.smesh.consul.client.http.SimpleHttpClient;
import com.github.lkq.smesh.consul.config.Config;
import com.github.lkq.smesh.consul.profile.Profile;
import com.github.lkq.smesh.consul.profile.ProfileFactory;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import org.slf4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;
import static org.slf4j.LoggerFactory.getLogger;

public class AppContextCluster extends AppContext {
    private static final Logger logger = getLogger(AppContextCluster.class);

    public static final int CLUSTER_SIZE = 3;
    public final String nodeName;
    public static final String ARTIFACT_VERSION = "0.1.0";
    public static final String ARTIFACT_NAME = "smesh-consul";

    public AppContextCluster(int nodeIndex) {
        this.nodeName = nodeName(nodeIndex);
    }

    public static AppContext node0() {
        return new AppContextCluster(0) {
            @Override
            public ConsulClient createConsulClient() {
                return new ConsulClient(new SimpleHttpClient(), new ResponseParser(), Constants.CONSUL_URL);
            }
        };
    }

    public static AppContext node1() {
        return new AppContextCluster(1) {
            @Override
            public Config createConfig() {
                Config config = super.createConfig();
                config.consulContext()
                        .portBindings(Arrays.asList(
                        new PortBinding("TCP", 8300, 9300),
                        new PortBinding("TCP", 8301, 9301),
                        new PortBinding("TCP", 8302, 9302),
                        new PortBinding("TCP", 8400, 9400),
                        new PortBinding("TCP", 8500, 9500),
                        new PortBinding("UDP", 8301, 9301),
                        new PortBinding("UDP", 8302, 9302)
                ));
                return config;
            }
            @Override
            public ConsulClient createConsulClient() {
                return new ConsulClient(new SimpleHttpClient(), new ResponseParser(), "http://localhost:9500");
            }
        };
    }

    public static AppContext node2() {
        return new AppContextCluster(2) {
            @Override
            public Config createConfig() {
                Config config = super.createConfig();
                config.consulContext()
                        .portBindings(Arrays.asList(
                                new PortBinding("TCP", 8300, 7300),
                                new PortBinding("TCP", 8301, 7301),
                                new PortBinding("TCP", 8302, 7302),
                                new PortBinding("TCP", 8400, 7400),
                                new PortBinding("TCP", 8500, 7500),
                                new PortBinding("UDP", 8301, 7301),
                                new PortBinding("UDP", 8302, 7302)
                        ));
                return config;
            }
            @Override
            public ConsulClient createConsulClient() {
                return new ConsulClient(new SimpleHttpClient(), new ResponseParser(), "http://localhost:7500");
            }
        };
    }

    public Config createConfig() {
        Config config = super.createConfig();

        String hostDataPath = ClassLoader.getSystemResource(".").getPath() + "data/" + nodeName + "-" + System.currentTimeMillis();

        if (new File(hostDataPath).mkdirs()) {
            logger.info("created config dir: {}", hostDataPath);
        }

        List<String> members = runningNodeIPs(createDockerClient());

        List<String> commands = new ArrayList<>(Arrays.asList("agent", "-server", "-ui", "-client=0.0.0.0"));
        if (members.size() == 0) {
            commands.add("-bootstrap");
        }

        List<String> retryJoins = members.stream().map(value -> "-retry-join=" + value).collect(Collectors.toList());
        commands.addAll(retryJoins);

        config.cleanStart(true)
                .consulContext()
                .hostName(nodeName)
                .nodeName(nodeName)
                .portBindings(Arrays.asList(
                        new PortBinding(8300),
                        new PortBinding(8301),
                        new PortBinding(8302),
                        new PortBinding(8400),
                        new PortBinding(8500),
                        new PortBinding("UDP", 8301, 8301),
                        new PortBinding("UDP", 8302, 8302)
                ))
                .volumeBindings(Collections.singletonList(new VolumeBinding(Constants.CONTAINER_DATA_PATH, hostDataPath)))
                .environmentVariables(Collections.singletonList("CONSUL_BIND_INTERFACE=eth0"))
                .commands(commands);
        return config;
    }

    @Override
    public ProfileFactory createProfileFactory(String nodeName) {
        ProfileFactory profileFactory = mock(ProfileFactory.class);
        Profile profile = new Profile().nodeName(this.nodeName)
                .name(ARTIFACT_NAME)
                .version(ARTIFACT_VERSION);
        willReturn(profile).given(profileFactory).create();
        return profileFactory;
    }

    private List<String> runningNodeIPs(SimpleDockerClient dockerClient) {
        List<String> runningNodeIPs = new ArrayList<>();
        for (int nodeIndex = 0; nodeIndex < CLUSTER_SIZE; nodeIndex++) {
            String nodeName = nodeName(nodeIndex);
            InspectContainerResponse memberNode = dockerClient.inspectContainer(nodeName);
            if (memberNode != null && memberNode.getState().getRunning() != null && memberNode.getState().getRunning()) {
                // collect cluster member ips
                String nodeIP = memberNode.getNetworkSettings().getNetworks().get("bridge").getIpAddress();
                logger.info("cluster node: {} ip: {}", nodeName, nodeIP);
                runningNodeIPs.add(nodeIP);
            } else {
                logger.info("node not found: {}", nodeName);
            }
        }
        return runningNodeIPs;
    }

    private String nodeName(int nodeIndex) {
        return "consul-server-" + nodeIndex;
    }
}
