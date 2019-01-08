package com.github.lkq.smesh.linkerd.app;

import com.github.lkq.instadocker.docker.entity.PortBinding;
import com.github.lkq.instadocker.docker.entity.VolumeBinding;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import com.github.lkq.smesh.linkerd.Constants;
import com.github.lkq.smesh.linkerd.Main;
import com.github.lkq.smesh.linkerd.config.Config;
import com.github.lkq.smesh.linkerd.config.LinkerdContext;
import com.github.lkq.smesh.linkerd.exception.SmeshLinkerdException;
import com.github.lkq.smesh.profile.Profile;
import com.github.lkq.smesh.profile.ProfileFactory;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

import static com.github.lkq.smesh.linkerd.Constants.VAR_CONSUL_HOST;

public class AppContext {

    public ProfileFactory createProfileFactory(String nodeName) {
        try {
            return new ProfileFactory(Main.class, nodeName);
        } catch (IOException e) {
            throw new SmeshLinkerdException("failed to create profile factory", e);
        }
    }

    public SimpleDockerClient createDockerClient() {
        return SimpleDockerClient.create();
    }

    public Logger createContainerLogger() {
        return LoggerFactory.getLogger("smesh-linkerd");
    }

    public Config createConfig() {
        Profile profile = createProfileFactory("dummy").create();
        String hostConfigFilePath = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String configFileName = Constants.LINKERD_CONFIG_FILE_PREFIX + "-" + profile.version() + ".yaml";
        return new Config()
                .cleanStart(true)
                .localLinkerdConfigPath(hostConfigFilePath)
                .linkerdContext(
                new LinkerdContext()
                        .imageName(Constants.LINKERD_IMAGE)
                        .hostName("linkerd")
                        .hostConfigFilePath(hostConfigFilePath)
                        .configFilePath(Constants.LINKERD_CONFIG_FILE_PATH)
                        .configFileName(configFileName)
                        .volumeBindings(Arrays.asList(
                                new VolumeBinding(Constants.LINKERD_CONFIG_FILE_PATH, hostConfigFilePath)
                        ))
                        .portBindings(Arrays.asList(
                                new PortBinding(8080),
                                new PortBinding(9990)
                        ))
                        .commands(Arrays.asList(String.join("/", Constants.LINKERD_CONFIG_FILE_PATH, configFileName)))
                        .templateVariables(ImmutableMap.of(VAR_CONSUL_HOST, "127.0.0.1"))
        );
    }
}
