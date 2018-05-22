package com.github.lkq.smesh.consul;

import com.github.lkq.smesh.AppVersion;
import com.github.lkq.smesh.Env;
import com.github.lkq.smesh.consul.client.ConsulClient;
import com.github.lkq.smesh.consul.client.ResponseParser;
import com.github.lkq.smesh.consul.client.http.SimpleHttpClient;
import com.github.lkq.smesh.consul.command.ConsulCommandBuilder;
import com.github.lkq.smesh.consul.aws.EC2;
import com.github.lkq.smesh.consul.aws.EC2Factory;
import com.github.lkq.smesh.docker.ContainerNetwork;
import com.github.lkq.smesh.logging.JulToSlf4jBridge;

import java.io.File;
import java.net.InetAddress;
import java.util.List;

public class Launcher {
    String ENV_NODE_NAME = "consul.node.name";
    String ENV_CONSUL_ROLE = "consul.node.role";
    String ENV_CONSUL_CLUSTER_MEMBERS = "consul.cluster.members";

    public static void main(String[] args) {
        JulToSlf4jBridge.setup();
        new Launcher().start();
    }

    private void start() {
        AppMaker appMaker = new AppMaker();

        EC2 ec2 = EC2Factory.get();

        boolean isEC2 = ec2.isEc2();

        String nodeName = getNodeName(ec2);
        List<String> clusterMembers = getClusterMembers(ec2, isEC2);

        ConsulCommandBuilder serverCommand = ConsulCommandBuilder.server(true, clusterMembers);

        String localDataPath = new File("").getAbsolutePath() + "/data/" + nodeName + "-" + System.currentTimeMillis();
        ConsulClient consulClient = new ConsulClient(new SimpleHttpClient(), new ResponseParser(), "http://localhost:8500");
        App app = appMaker.makeApp(0, nodeName,
                ContainerNetwork.CONSUL_SERVER,
                serverCommand,
                localDataPath,
                AppVersion.get(App.class), consulClient
        );

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        app.start(true);
    }

    private String getNodeName(EC2 ec2) {
        return ec2.isEc2() ? ec2.getTagValue(ENV_NODE_NAME) : InetAddress.getLoopbackAddress().getHostName();
    }

    private List<String> getClusterMembers(EC2 ec2, boolean isEC2) {
        return isEC2 ? ec2.getInstanceIPByTagValue(ENV_CONSUL_ROLE, "client") : Env.getList(ENV_CONSUL_CLUSTER_MEMBERS, " ");
    }


}
