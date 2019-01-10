package com.github.lkq.smesh.test.linkerd;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.lkq.smesh.linkerd.app.AppContext;
import com.github.lkq.smesh.linkerd.config.Config;
import com.github.lkq.smesh.linkerd.profile.Profile;
import com.github.lkq.smesh.linkerd.profile.ProfileFactory;
import com.github.lkq.smesh.test.DockerClientFactory;
import org.slf4j.Logger;

import java.util.HashMap;

import static com.github.lkq.smesh.linkerd.Constants.VAR_CONSUL_HOST;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;
import static org.slf4j.LoggerFactory.getLogger;

public class LinkerdContextLocal extends AppContext {
    private static final Logger logger = getLogger(LinkerdContextLocal.class);

    public static final String NODE_NAME = "linkerd";
    public static final String ARTIFACT_VERSION = "0.1.0";
    public static final String ARTIFACT_NAME = "smesh-linkerd";

    private String consulContainer;

    public LinkerdContextLocal(String consulContainer) {
        this.consulContainer = consulContainer;
    }

    @Override
    public Config createConfig() {
        Config config = super.createConfig();
        config.linkerdContext().templateVariables(createTemplateVariables(consulContainer));
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

    private HashMap<String, String> createTemplateVariables(String consulContainer) {
        InspectContainerResponse inspectConsulContainer = DockerClientFactory.create().inspectContainerCmd(consulContainer).exec();
        String consulIP = inspectConsulContainer.getNetworkSettings().getNetworks().get("bridge").getIpAddress();
        HashMap<String, String> configVariables = new HashMap<>();
        configVariables.put(VAR_CONSUL_HOST, consulIP);
        return configVariables;
    }
}
