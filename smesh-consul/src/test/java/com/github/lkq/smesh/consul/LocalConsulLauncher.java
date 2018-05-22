package com.github.lkq.smesh.consul;

import com.github.lkq.smesh.consul.client.ConsulClient;
import com.github.lkq.smesh.consul.client.ResponseParser;
import com.github.lkq.smesh.consul.client.http.SimpleHttpClient;
import com.github.lkq.smesh.consul.command.ConsulCommandBuilder;
import com.github.lkq.smesh.docker.ContainerNetwork;
import com.github.lkq.smesh.logging.JulToSlf4jBridge;

import java.util.Collections;

public class LocalConsulLauncher {

    public static void main(String[] args) {
        JulToSlf4jBridge.setup();
        new LocalConsulLauncher().start();
    }

    public void start() {
        AppMaker appMaker = new AppMaker();

        ConsulCommandBuilder serverCommand = ConsulCommandBuilder.server(true, Collections.emptyList())
                .ui(true)
                .clientIP("0.0.0.0")
                .bootstrap(true);

        String nodeName = "consul";
        String hostDataPath = ClassLoader.getSystemResource(".").getPath() + "data/" + nodeName + "-" + System.currentTimeMillis();
        ConsulClient consulClient = new ConsulClient(new SimpleHttpClient(), new ResponseParser(), "http://localhost:8500");
        App app = appMaker.makeApp(
                1025,
                nodeName,
                ContainerNetwork.CONSUL_MAC,
                serverCommand,
                hostDataPath,
                "1.2.3", consulClient);

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        app.start(true);
    }
}
