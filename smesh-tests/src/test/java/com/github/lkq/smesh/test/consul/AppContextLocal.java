package com.github.lkq.smesh.test.consul;

import com.github.lkq.instadocker.docker.entity.PortBinding;
import com.github.lkq.instadocker.docker.entity.VolumeBinding;
import com.github.lkq.smesh.consul.Constants;
import com.github.lkq.smesh.consul.app.AppContext;
import com.github.lkq.smesh.consul.config.Config;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import com.github.lkq.smesh.profile.Profile;
import com.github.lkq.smesh.profile.ProfileFactory;
import org.slf4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;
import static org.slf4j.LoggerFactory.getLogger;

public class AppContextLocal extends AppContext {
    private static final Logger logger = getLogger(AppContextLocal.class);

    public static final String NODE_NAME = "consul-master";
    public static final String ARTIFACT_VERSION = "0.1.0";
    public static final String ARTIFACT_NAME = "smesh-consul";

    public Config createConfig() {
        Config config = super.createConfig();

        String hostDataPath = ClassLoader.getSystemResource(".").getPath() + "data/" + NODE_NAME + "-" + System.currentTimeMillis();

        if (new File(hostDataPath).mkdirs()) {
            logger.info("created config dir: {}", hostDataPath);
        }

        config.cleanStart(true)
                .consulContext()
                .hostName(NODE_NAME)
                .nodeName(NODE_NAME)
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

    @Override
    public ProfileFactory createProfileFactory(String nodeName) {
        ProfileFactory profileFactory = mock(ProfileFactory.class);
        Profile profile = new Profile().nodeName(NODE_NAME)
                .name(ARTIFACT_NAME)
                .version(ARTIFACT_VERSION);
        willReturn(profile).given(profileFactory).create();
        return profileFactory;
    }

    public SimpleDockerClient createDockerClient() {
        return SimpleDockerClient.create();
    }
}
