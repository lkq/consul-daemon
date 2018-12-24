package com.github.lkq.smesh.consul.app;

import com.github.lkq.smesh.consul.config.Config;
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
}
