package com.github.lkq.smesh.linkerd.app;

import com.github.lkq.smesh.docker.SimpleDockerClient;
import com.github.lkq.smesh.linkerd.config.Config;
import com.github.lkq.smesh.profile.ProfileFactory;
import com.google.gson.Gson;
import dagger.Module;
import dagger.Provides;
import org.slf4j.Logger;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class AppModule {

    private AppContext appContext;

    public AppModule(AppContext appContext) {
        this.appContext = appContext;
    }

    @Provides @Singleton
    public Gson gson() {
        return new Gson();
    }

    @Provides @Singleton
    public Config config() {
        return appContext.createConfig();
    }

    @Provides @Singleton
    public SimpleDockerClient dockerClient() {
        return appContext.createDockerClient();
    }

    @Provides @Singleton
    public ProfileFactory profileFactory(Config config) {
        return appContext.createProfileFactory(config.linkerdContext().nodeName());
    }

    @Provides @Singleton @Named("containerLogger")
    public Logger containerLogger() {
        return appContext.createContainerLogger();
    }
}
