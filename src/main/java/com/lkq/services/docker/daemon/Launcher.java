package com.lkq.services.docker.daemon;

import com.lkq.services.docker.daemon.consul.ConsulAPI;
import com.lkq.services.docker.daemon.consul.ConsulController;
import com.lkq.services.docker.daemon.consul.ConsulResponseParser;
import com.lkq.services.docker.daemon.consul.context.ConsulContext;
import com.lkq.services.docker.daemon.consul.context.ConsulContextFactory;
import com.lkq.services.docker.daemon.container.DockerClientFactory;
import com.lkq.services.docker.daemon.container.SimpleDockerClient;
import com.lkq.services.docker.daemon.env.Environment;
import com.lkq.services.docker.daemon.health.ConsulHealthChecker;
import com.lkq.services.docker.daemon.routes.v1.Routes;
import com.lkq.services.docker.daemon.utils.HttpClientFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.logging.LogManager;

public class Launcher {
    public static void main(String[] args) {
        initLogging();
        new Launcher().start();
    }

    private void start() {
        ConsulContext context = new ConsulContextFactory().createClusterNodeContext();
        ConsulAPI consulAPI = new ConsulAPI(new HttpClientFactory().create(), new ConsulResponseParser(), Environment.get().consulAPIPort());
        SimpleDockerClient dockerClient = SimpleDockerClient.create(DockerClientFactory.get());

        String appVersion = Environment.get().appVersion();
        ConsulHealthChecker consulHealthChecker = new ConsulHealthChecker(consulAPI, context.nodeName(), appVersion);
        ConsulController consulController = new ConsulController(dockerClient, consulHealthChecker);
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

    private static void initLogging() {
        // redirect jul to slf4j
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }
}
