package com.github.lkq.smesh.consul;


import com.github.lkq.smesh.consul.app.App;
import com.github.lkq.smesh.consul.app.AppContext;
import com.github.lkq.smesh.consul.app.AppModule;
import com.github.lkq.smesh.consul.utils.ConsulUtils;
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

        int port = ConsulUtils.parseInt(args.length > 0 ? args[0] : null, 0);
        app.start(port);
    }
}
