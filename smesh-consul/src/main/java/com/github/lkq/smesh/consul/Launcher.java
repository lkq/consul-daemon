package com.github.lkq.smesh.consul;

import com.github.lkq.smesh.AppVersion;
import com.github.lkq.smesh.Env;
import com.github.lkq.smesh.consul.aws.EC2;
import com.github.lkq.smesh.consul.aws.EC2Factory;
import com.github.lkq.smesh.consul.client.ConsulClient;
import com.github.lkq.smesh.consul.client.ResponseParser;
import com.github.lkq.smesh.consul.client.http.SimpleHttpClient;
import com.github.lkq.smesh.consul.command.ConsulCommandBuilder;
import com.github.lkq.smesh.docker.ContainerNetwork;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.File;
import java.net.InetAddress;
import java.util.List;
import java.util.logging.LogManager;

public class Launcher {

    public static void main(String[] args) {
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        new Launcher().start();
    }

    private void start() {
        AppMaker appMaker = new AppMaker();

        EC2 ec2 = EC2Factory.get();

        String nodeName = getNodeName(ec2);
        List<String> clusterMembers = getClusterMembers(ec2);

        ConsulCommandBuilder serverCommand = ConsulCommandBuilder.server(true, clusterMembers);

        String hostDataPath = new File("").getAbsolutePath() + "/data/" + nodeName + "-" + System.currentTimeMillis();
        ConsulClient consulClient = new ConsulClient(new SimpleHttpClient(), new ResponseParser(), Constants.CONSUL_URL);
        App app = appMaker.makeApp(0, nodeName,
                ContainerNetwork.CONSUL_SERVER,
                serverCommand,
                hostDataPath,
                AppVersion.get(App.class), consulClient
        );

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        app.start(true);
    }

    private String getNodeName(EC2 ec2) {
        return ec2.isEc2() ? ec2.getTagValue(Constants.ENV_NODE_NAME) : InetAddress.getLoopbackAddress().getHostName();
    }

    private List<String> getClusterMembers(EC2 ec2) {
        return ec2.isEc2() ? ec2.getInstanceIPByTagValue(Constants.ENV_CONSUL_ROLE, "server") : Env.getList(Constants.ENV_CONSUL_CLUSTER_MEMBERS, " ");
    }


}
