package com.lkq.services.docker.daemon;

import com.lkq.services.docker.daemon.consul.ConsulController;
import com.lkq.services.docker.daemon.consul.ConsulHealthChecker;
import com.lkq.services.docker.daemon.consul.context.ConsulContext;
import com.lkq.services.docker.daemon.container.DockerClientFactory;
import com.lkq.services.docker.daemon.container.SimpleDockerClient;
import com.lkq.services.docker.daemon.handler.HealthCheckHandler;
import com.lkq.services.docker.daemon.routes.v1.Routes;
import org.eclipse.jetty.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static Logger logger = LoggerFactory.getLogger(App.class);

    private final ConsulController consulController;
    private final SimpleDockerClient dockerClient;

    public App() {

        dockerClient = SimpleDockerClient.create(DockerClientFactory.get());
        consulController = new ConsulController(
                dockerClient,
                new ConsulHealthChecker());
    }

    public void start(ConsulContext context) {

        HttpClient httpClient = new HttpClient();
        try {
            httpClient.start();
        } catch (Exception e) {
            logger.error("failed to start http client", e);
        }

        consulController.start(context);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> consulController.stop(context.nodeName())));

        new Routes(new HealthCheckHandler(consulController, httpClient)).ignite();

    }

    public ConsulController getConsulController() {
        return consulController;
    }

    public SimpleDockerClient getDockerClient() {
        return dockerClient;
    }
}
