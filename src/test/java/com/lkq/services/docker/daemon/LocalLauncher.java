package com.lkq.services.docker.daemon;

import com.lkq.services.docker.daemon.consul.ConsulAPI;
import com.lkq.services.docker.daemon.consul.ConsulController;
import com.lkq.services.docker.daemon.consul.ConsulResponseParser;
import com.lkq.services.docker.daemon.consul.command.AgentCommandBuilder;
import com.lkq.services.docker.daemon.consul.context.ConsulContext;
import com.lkq.services.docker.daemon.consul.context.ConsulContextFactory;
import com.lkq.services.docker.daemon.container.DockerClientFactory;
import com.lkq.services.docker.daemon.container.PortBinder;
import com.lkq.services.docker.daemon.container.SimpleDockerClient;
import com.lkq.services.docker.daemon.env.Environment;
import com.lkq.services.docker.daemon.env.EnvironmentProvider;
import com.lkq.services.docker.daemon.health.ConsulHealthChecker;
import com.lkq.services.docker.daemon.logging.JulToSlf4jBridge;
import com.lkq.services.docker.daemon.routes.v1.Routes;
import com.lkq.services.docker.daemon.utils.HttpClientFactory;

import java.util.List;

public class LocalLauncher {
    public static void main(String[] args) {
        JulToSlf4jBridge.setup();
        new LocalLauncher().launch(new MacEnvironment(), new ConsulPorts().defaultPortBindings());
    }

    public void launch(Environment env, List<PortBinder> portBinders) {
        EnvironmentProvider.set(env);

        AgentCommandBuilder builder = new AgentCommandBuilder()
                .server(true)
                .ui(true)
                .clientIP("0.0.0.0")
                .bootstrap(true);
        ConsulContext context = new ConsulContextFactory()
                .createDefaultContext(Environment.get().nodeName())
                .portBinders(portBinders)
                .commandBuilder(builder);

        ConsulAPI consulAPI = new ConsulAPI(new HttpClientFactory().create(), new ConsulResponseParser(), Environment.get().consulAPIPort());
        SimpleDockerClient dockerClient = SimpleDockerClient.create(DockerClientFactory.get());

        ConsulHealthChecker consulHealthChecker = new ConsulHealthChecker(consulAPI, context.nodeName(), Environment.get().appVersion());
        ConsulController consulController = new ConsulController(dockerClient);
        WebServer webServer = new WebServer(new Routes(consulHealthChecker), 0);

        App app = new App(context,
                consulController,
                consulHealthChecker,
                webServer,
                Environment.get().appVersion());

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        app.start(Environment.get().forceRestart());
    }
}
