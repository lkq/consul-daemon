package com.github.lkq.smesh.consul;

import com.github.lkq.smesh.consul.client.ConsulClient;
import com.github.lkq.smesh.consul.command.ConsulCommandBuilder;
import com.github.lkq.smesh.consul.container.ConsulController;
import com.github.lkq.smesh.consul.context.ConsulContextFactory;
import com.github.lkq.smesh.consul.handler.AppInfo;
import com.github.lkq.smesh.consul.routes.v1.ConsulRoutes;
import com.github.lkq.smesh.consul.routes.v1.RegistrationRoutes;
import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.context.VolumeBinding;
import com.github.lkq.smesh.docker.ContainerNetwork;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import com.github.lkq.smesh.server.WebServer;
import com.github.lkq.timeron.Timer;

import java.util.ArrayList;
import java.util.List;

public class AppMaker {

    public App makeApp(int restPort, String nodeName, ContainerNetwork network, ConsulCommandBuilder commandBuilder, String hostDataPath, String appVersion, ConsulClient consulClient) {

        final ConsulContextFactory contextFactory = new ConsulContextFactory();

        ContainerContext context = contextFactory.create(nodeName, network.network(), getEnv(), commandBuilder)
                .portBindings(network.portBindings())
                .volumeBindings(new VolumeBinding(hostDataPath, Constants.CONTAINER_DATA_PATH));

        VersionRegister versionRegister = new VersionRegister(consulClient, Constants.APP_NAME + "-" + nodeName, appVersion, 10000);

        AppInfo appInfo = new AppInfo(consulClient, context.nodeName());
        ConsulController consulController = new ConsulController(SimpleDockerClient.create());
        WebServer webServer = new WebServer(restPort, new RegistrationRoutes(consulClient), new ConsulRoutes(appInfo));

        return new App(context,
                consulController,
                appInfo,
                versionRegister,
                webServer,
                appVersion
        );
    }

    private static void setupTimers(Timer timer) {
        SimpleDockerClient interceptor = timer.interceptor(SimpleDockerClient.class);
        timer.measure(() -> interceptor.createContainer("", ""));
        timer.measure(() -> interceptor.pullImage(""));
        timer.measure(() -> interceptor.startContainer(""));
        timer.measure(() -> interceptor.stopContainer(""));
        timer.measure(() -> interceptor.execute("", null));
    }

    public List<String> getEnv() {
        List<String> env = new ArrayList<>();
        env.add("CONSUL_BIND_INTERFACE=eth0");
        return env;
    }
}
