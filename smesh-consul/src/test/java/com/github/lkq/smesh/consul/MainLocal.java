package com.github.lkq.smesh.consul;

import com.github.lkq.smesh.consul.app.App;
import com.github.lkq.smesh.consul.app.AppModule;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AppModule.class})
public interface MainLocal {

    com.github.lkq.smesh.consul.app.App app();

    static void main(String[] args) {
        Main appMain = DaggerMain.builder()
                .appModule(new AppModule(new AppContextLocal()))
                .build();
        App app = appMain.app();
        app.start(0);
    }
}
