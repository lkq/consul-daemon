package com.github.lkq.smesh.consul;

import com.github.lkq.smesh.consul.api.ConsulAPI;
import com.github.lkq.smesh.consul.api.ConsulController;
import com.github.lkq.smesh.consul.api.ConsulResponseParser;
import com.github.lkq.smesh.consul.context.ConsulContextFactory;
import com.github.lkq.smesh.consul.env.Environment;
import com.github.lkq.smesh.consul.health.ConsulHealthChecker;
import com.github.lkq.smesh.consul.routes.v1.Routes;
import com.github.lkq.smesh.consul.utils.HttpClientFactory;
import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.docker.DockerClientFactory;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import com.github.lkq.smesh.logging.JulToSlf4jBridge;

public class Launcher {
    public static void main(String[] args) {
        JulToSlf4jBridge.setup();
        new Launcher().start();
    }

    private void start() {
        ConsulAPI consulAPI = new ConsulAPI(new HttpClientFactory().create(), new ConsulResponseParser(), Environment.get().consulAPIPort());
        SimpleDockerClient dockerClient = SimpleDockerClient.create(DockerClientFactory.get());

        ContainerContext context = new ConsulContextFactory().createClusterNodeContext();
        String appVersion = Environment.get().appVersion();
        ConsulHealthChecker consulHealthChecker = new ConsulHealthChecker(consulAPI, context.nodeName(), appVersion);
        ConsulController consulController = new ConsulController(dockerClient);
        WebServer webServer = new WebServer(new Routes(consulHealthChecker), Environment.get().servicePort());

        App app = new App(context,
                consulController,
                consulHealthChecker,
                webServer,
                appVersion
        );

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        app.start(Environment.get().forceRestart());
    }

}
