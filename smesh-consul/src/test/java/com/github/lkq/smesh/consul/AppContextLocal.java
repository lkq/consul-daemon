package com.github.lkq.smesh.consul;

import com.github.lkq.instadocker.docker.entity.PortBinding;
import com.github.lkq.instadocker.docker.entity.VolumeBinding;
import com.github.lkq.smesh.consul.app.AppContext;
import com.github.lkq.smesh.consul.config.Config;
import com.github.lkq.smesh.docker.SimpleDockerClient;

import java.util.Arrays;
import java.util.Collections;

public class AppContextLocal extends AppContext {

    public Config createConfig() {
        Config config = super.createConfig();

        String nodeName = "consul-master";
        String hostDataPath = ClassLoader.getSystemResource(".").getPath() + "data/" + nodeName + "-" + System.currentTimeMillis();

        config.consulContext()
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
                .commands(Arrays.asList("agent", "-server", "-ui", "-bootstrap", "-bootstrap-expect=1", "-client=0.0.0.0"));
        return config;
    }

    public SimpleDockerClient createDockerClient() {
        return SimpleDockerClient.create();
    }
}
