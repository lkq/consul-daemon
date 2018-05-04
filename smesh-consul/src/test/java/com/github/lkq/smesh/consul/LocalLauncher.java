package com.github.lkq.smesh.consul;

import com.github.lkq.smesh.consul.command.ConsulCommandBuilder;
import com.github.lkq.smesh.context.PortBinding;
import com.github.lkq.smesh.server.WebServer;
import com.github.lkq.smesh.consul.api.ConsulAPI;
import com.github.lkq.smesh.consul.container.ConsulController;
import com.github.lkq.smesh.consul.api.ConsulResponseParser;
import com.github.lkq.smesh.consul.context.ConsulContextFactory;
import com.github.lkq.smesh.consul.env.Environment;
import com.github.lkq.smesh.consul.env.EnvironmentProvider;
import com.github.lkq.smesh.consul.health.ConsulHealthChecker;
import com.github.lkq.smesh.consul.routes.v1.ConsulRoutes;
import com.github.lkq.smesh.consul.utils.HttpClientFactory;
import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.docker.DockerClientFactory;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import com.github.lkq.smesh.logging.JulToSlf4jBridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LocalLauncher {
    private static Logger logger;
    public static void main(String[] args) {
        JulToSlf4jBridge.setup();
        logger = LoggerFactory.getLogger(LocalLauncher.class);
        try {
            new LocalLauncher().launch(new MacEnvironment(), new ConsulPorts().defaultPortBindings());
        } catch (Exception e) {
            logger.error("failed to start application", e);
            System.exit(1);
        }
    }

    public void launch(Environment env, List<PortBinding> portBindings) {
        EnvironmentProvider.set(env);

        ConsulCommandBuilder builder = new ConsulCommandBuilder()
                .server(true)
                .ui(true)
                .clientIP("0.0.0.0")
                .bootstrap(true);
        ContainerContext context = new ConsulContextFactory()
                .createDefaultContext(Environment.get().nodeName())
                .portBinders(portBindings)
                .commandBuilder(builder);

        ConsulAPI consulAPI = new ConsulAPI(new HttpClientFactory().create(), new ConsulResponseParser(), Environment.get().consulAPIPort());
        SimpleDockerClient dockerClient = SimpleDockerClient.create(DockerClientFactory.get());

        ConsulHealthChecker consulHealthChecker = new ConsulHealthChecker(consulAPI, context.nodeName(), Environment.get().appVersion());
        ConsulController consulController = new ConsulController(dockerClient);
        WebServer webServer = new WebServer(new ConsulRoutes(consulHealthChecker), 0);

        App app = new App(context,
                consulController,
                consulHealthChecker,
                webServer,
                Environment.get().appVersion());

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        app.start(Environment.get().forceRestart());
    }
}
