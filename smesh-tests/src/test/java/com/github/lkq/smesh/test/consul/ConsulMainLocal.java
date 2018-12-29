package com.github.lkq.smesh.test.consul;

import com.github.lkq.smesh.consul.DaggerMain;
import com.github.lkq.smesh.consul.Main;
import com.github.lkq.smesh.consul.app.App;
import com.github.lkq.smesh.consul.app.AppModule;

public class ConsulMainLocal {

    static App app;

    public static void main(String[] args) {
        start(0);
    }

    public static String start(int port) {
        Main appMain = DaggerMain.builder()
                .appModule(new AppModule(new AppContextLocal()))
                .build();
        app = appMain.app();

        app.start(port);

        return app.instaDocker().container().containerId().orElse(null);
    }

    public static void stop() {
        app.stop();
    }
}
