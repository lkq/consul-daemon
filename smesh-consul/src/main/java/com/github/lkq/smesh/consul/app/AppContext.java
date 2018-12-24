package com.github.lkq.smesh.consul.app;

import com.github.lkq.smesh.consul.config.Config;

public class AppContext {

    public Config createConfig() {
        return new Config().imageName("consul:1.0.6")
                .containerName("smesh-consul");
    }
}
