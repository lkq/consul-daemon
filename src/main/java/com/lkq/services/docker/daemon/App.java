package com.lkq.services.docker.daemon;

import com.lkq.services.docker.daemon.consul.ConsulController;
import com.lkq.services.docker.daemon.consul.ConsulHealthChecker;
import com.lkq.services.docker.daemon.consul.context.ConsulContext;
import com.lkq.services.docker.daemon.container.ContainerLogRedirector;
import com.lkq.services.docker.daemon.container.DockerClientFactory;
import com.lkq.services.docker.daemon.container.SimpleDockerClient;
import com.lkq.services.docker.daemon.handler.HealthCheckHandler;
import com.lkq.services.docker.daemon.routes.v1.Routes;

public class App {
    private final ConsulController consulController;
    private final SimpleDockerClient dockerClient;

    public App() {

        dockerClient = SimpleDockerClient.create(DockerClientFactory.get());
        consulController = new ConsulController(
                dockerClient,
                new ConsulHealthChecker(),
                new ContainerLogRedirector());
    }

    public void start(ConsulContext context) {

        consulController.start(context);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> consulController.stop(context.nodeName())));

        new Routes(new HealthCheckHandler(consulController)).ignite();

    }

    public ConsulController getConsulController() {
        return consulController;
    }

    public SimpleDockerClient getDockerClient() {
        return dockerClient;
    }
}
