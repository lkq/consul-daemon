package com.github.lkq.smesh.linkerd;

import com.github.lkq.smesh.linkerd.app.AppContext;
import com.github.lkq.smesh.linkerd.config.Config;
import com.github.lkq.smesh.linkerd.profile.Profile;
import com.github.lkq.smesh.linkerd.profile.ProfileFactory;
import com.google.common.collect.ImmutableMap;

import static com.github.lkq.smesh.linkerd.Constants.VAR_CONSUL_HOST;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;

public class AppContextLocal extends AppContext {
    public static final String ARTIFACT_VERSION = "0.1.0";
    public static final String ARTIFACT_NAME = "smesh-linkerd";

    @Override
    public ProfileFactory createProfileFactory(String nodeName) {
        ProfileFactory profileFactory = mock(ProfileFactory.class);
        Profile profile = new Profile().nodeName("local-linkerd")
                .name(ARTIFACT_NAME)
                .version(ARTIFACT_VERSION);
        willReturn(profile).given(profileFactory).create();
        return profileFactory;
    }

    @Override
    public Config createConfig() {
        Config config = super.createConfig();
        config.linkerdContext().templateVariables(ImmutableMap.of(VAR_CONSUL_HOST, "127.0.0.2"));
        return config;
    }
}
