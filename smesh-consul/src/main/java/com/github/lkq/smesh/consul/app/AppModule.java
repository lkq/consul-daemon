package com.github.lkq.smesh.consul.app;

import com.github.lkq.smesh.consul.client.ConsulClient;
import com.github.lkq.smesh.consul.client.ServiceRegistrar;
import com.github.lkq.smesh.consul.config.Config;
import com.github.lkq.smesh.consul.profile.ProfileFactory;
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
    public ProfileFactory profileFactory(Config config) {
        return appContext.createProfileFactory(config.consulContext().nodeName());
    }

    @Provides @Singleton
    public ConsulClient consulClient() {
        return appContext.createConsulClient();
    }

    @Provides @Singleton
    public ServiceRegistrar serviceRegistrar(ConsulClient client) {
        return new ServiceRegistrar(client);
    }

    @Provides @Singleton @Named("containerLogger")
    public Logger containerLogger() {
        return appContext.createContainerLogger();
    }
}
