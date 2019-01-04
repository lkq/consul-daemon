package com.github.lkq.smesh.consul;

import com.github.lkq.smesh.consul.app.AppContext;
import com.github.lkq.smesh.consul.app.AppModule;

/**
 * start server nodes and form a cluster
 */
public class ClusterNode {

    public String startNode(int httpPort, AppContext appContext) {
        Main appMain = DaggerMain.builder()
                .appModule(new AppModule(appContext))
                .build();
        com.github.lkq.smesh.consul.app.App app = appMain.app();

        app.start(httpPort);

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
        return app.instaDocker().container().containerId().orElse(null);
    }
}
