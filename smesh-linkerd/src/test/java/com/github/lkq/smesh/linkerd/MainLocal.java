package com.github.lkq.smesh.linkerd;


import com.github.lkq.smesh.linkerd.app.AppModule;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AppModule.class})
public interface MainLocal {

    com.github.lkq.smesh.linkerd.app.App app();

    static void main(String[] args) {
        Main appMain = DaggerMain.builder()
                .appModule(new AppModule(new AppContextLocal()))
                .build();
        com.github.lkq.smesh.linkerd.app.App app = appMain.app();

        app.start(0);
    }
}
