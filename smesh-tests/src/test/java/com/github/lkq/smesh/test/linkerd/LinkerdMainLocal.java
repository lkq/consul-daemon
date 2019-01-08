package com.github.lkq.smesh.test.linkerd;


import com.github.lkq.smesh.linkerd.DaggerMain;
import com.github.lkq.smesh.linkerd.Main;
import com.github.lkq.smesh.linkerd.app.App;
import com.github.lkq.smesh.linkerd.app.AppModule;

public class LinkerdMainLocal {

    static App app;

    public static void main(String[] args) {
        start(0, "");
    }

    public static String start(int port, String consulContainer) {
        Main appMain = DaggerMain.builder()
                .appModule(new AppModule(new LinkerdContextLocal(consulContainer)))
                .build();
        app = appMain.app();

        app.start(port);

        return app.instaDocker().container().containerId().orElse(null);
    }

    public static void stop() {
        app.stop();
    }
}
