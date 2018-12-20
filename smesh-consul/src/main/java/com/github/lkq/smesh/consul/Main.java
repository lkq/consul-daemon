package com.github.lkq.smesh.consul;


import com.github.lkq.smesh.consul.app.AppContext;
import com.github.lkq.smesh.consul.app.AppModule;
import com.github.lkq.smesh.consul.app.App;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AppModule.class})
public interface Main {

    App app();

    static void main(String[] args) {
        Main appMain = DaggerMain.builder()
                .appModule(new AppModule(new AppContext()))
                .build();
        App app = appMain.app();
        app.start(0);
    }
}
