package com.github.lkq.smesh.consul;

import com.github.lkq.smesh.AppVersion;
import com.github.lkq.smesh.consul.api.ConsulAPI;
import com.github.lkq.smesh.consul.api.ConsulResponseParser;
import com.github.lkq.smesh.consul.container.ConsulController;
import com.github.lkq.smesh.consul.context.ConsulContextFactory;
import com.github.lkq.smesh.consul.env.Environment;
import com.github.lkq.smesh.consul.health.ConsulHealthChecker;
import com.github.lkq.smesh.consul.routes.v1.ConsulRoutes;
import com.github.lkq.smesh.consul.utils.HttpClientFactory;
import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.docker.DockerClientFactory;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import com.github.lkq.smesh.server.WebServer;
import com.github.lkq.timeron.Timer;

import java.util.ArrayList;
import java.util.List;

public class AppMaker {

    public App makeApp(String nodeName, String network, boolean runAsServer, List<String> clusterMembers, List<String> env) {
        Timer timer = new Timer();
        setupTimers(timer);

        String appVersion = AppVersion.get(App.class);

        ConsulAPI consulAPI = new ConsulAPI(new HttpClientFactory().create(), new ConsulResponseParser(), Environment.get().consulAPIPort());

        SimpleDockerClient dockerClient = timer.on(SimpleDockerClient.create(DockerClientFactory.get()));

        final ConsulContextFactory consulContextFactory = new ConsulContextFactory();
        ContainerContext context = consulContextFactory.createClusterNodeContext(nodeName, network, runAsServer, clusterMembers, env);
        ConsulHealthChecker consulHealthChecker = new ConsulHealthChecker(consulAPI, context.nodeName(), appVersion);
        ConsulController consulController = new ConsulController(dockerClient);
        WebServer webServer = new WebServer(new ConsulRoutes(consulHealthChecker), Environment.get().servicePort());

        return new App(context,
                consulController,
                consulHealthChecker,
                webServer,
                appVersion
        );
    }


    public List<String> getEnvironmentVariables() {
        List<String> env = new ArrayList<>();
        env.add("CONSUL_BIND_INTERFACE=eth0");
        return env;
    }

    private static void setupTimers(Timer timer) {
        SimpleDockerClient interceptor = timer.interceptor(SimpleDockerClient.class);
        timer.measure(() -> interceptor.createContainer("", ""));
        timer.measure(() -> interceptor.pullImage(""));
        timer.measure(() -> interceptor.startContainer(""));
        timer.measure(() -> interceptor.stopContainer(""));
        timer.measure(() -> interceptor.execute("", null));
    }
}
