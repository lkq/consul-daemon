package com.github.lkq.smesh.consul.app;

import com.github.lkq.smesh.consul.client.ConsulClient;
import com.github.lkq.smesh.consul.config.Config;
import com.github.lkq.smesh.consul.profile.ProfileFactory;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class AppModule {

    private AppContext appContext;

    public AppModule(AppContext appContext) {
        this.appContext = appContext;
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
        return appContext.createProfileFactory(config.consulContext().nodeName());
    }

    @Provides @Singleton
    public ConsulClient consulClient() {
        return appContext.createConsulClient();
    }
}
