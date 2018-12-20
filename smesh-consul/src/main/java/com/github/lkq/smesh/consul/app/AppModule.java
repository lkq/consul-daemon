package com.github.lkq.smesh.consul.app;

import dagger.Module;

@Module
public class AppModule {

    private AppContext appContext;

    public AppModule(AppContext appContext) {
        this.appContext = appContext;
    }

}
