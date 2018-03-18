package com.lkq.services.docker.daemon;

import com.lkq.services.docker.daemon.consul.ConsulAPI;
import com.lkq.services.docker.daemon.consul.ConsulController;
import com.lkq.services.docker.daemon.consul.ConsulHealthChecker;
import com.lkq.services.docker.daemon.consul.ConsulResponseParser;
import com.lkq.services.docker.daemon.consul.context.ConsulContext;
import com.lkq.services.docker.daemon.container.DockerClientFactory;
import com.lkq.services.docker.daemon.container.SimpleDockerClient;
import com.lkq.services.docker.daemon.env.Environment;
import com.lkq.services.docker.daemon.health.HealthCheckHandler;
import com.lkq.services.docker.daemon.routes.v1.Routes;
import com.lkq.services.docker.daemon.utils.HttpClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import static spark.Spark.port;

public class App {
    private static Logger logger = LoggerFactory.getLogger(App.class);

    private ConsulContext context;
    private ConsulController consulController;

    public App(ConsulContext context) {
        this.context = context;
    }

    public void start() {

        String jarVersion = Environment.get().jarVersion();

        ConsulAPI consulAPI = new ConsulAPI(new HttpClientFactory().create(), new ConsulResponseParser(), Environment.get().consulAPIPort());
        ConsulHealthChecker consulHealthChecker = new ConsulHealthChecker(consulAPI, context.nodeName(), jarVersion);

        SimpleDockerClient dockerClient = SimpleDockerClient.create(DockerClientFactory.get());
        consulController = new ConsulController(dockerClient, consulHealthChecker);

        String currentDaemonVersion = consulHealthChecker.getCurrentDaemonVersion();
        boolean forceRestart = shouldForceRestart(jarVersion, currentDaemonVersion);
        logger.info("old version: {}, new version: {}, force restart: {}", currentDaemonVersion, jarVersion, forceRestart);
        consulController.start(context, forceRestart);
        // TODO: find a better solution to block until consul started
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        consulHealthChecker.registerDaemonVersion(jarVersion);
        port(Environment.get().servicePort());
        new Routes(new HealthCheckHandler(context.nodeName(), new HttpClientFactory().create())).ignite();

    }

    private boolean shouldForceRestart(String jarVersion, String currentDaemonVersion) {
        Boolean forceRestart = Environment.get().forceRestart();
        if (forceRestart == null) {
            if (StringUtils.isNotEmpty(currentDaemonVersion) && currentDaemonVersion.startsWith(jarVersion)) {
                return false;
            } else {
                return true;
            }
        } else {
            return forceRestart;
        }
    }

    public void stop() {
        consulController.stop(context.nodeName());
    }

}
