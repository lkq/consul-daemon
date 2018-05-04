package com.github.lkq.smesh.consul;

import com.github.lkq.smesh.Env;
import com.github.lkq.smesh.consul.command.ConsulCommandBuilder;
import com.github.lkq.smesh.consul.env.Environment;
import com.github.lkq.smesh.consul.env.aws.EC2;
import com.github.lkq.smesh.consul.env.aws.EC2Factory;
import com.github.lkq.smesh.logging.JulToSlf4jBridge;

import java.util.ArrayList;
import java.util.List;

public class Launcher {
    String ENV_NODE_NAME = "consul.nodeName";
    String ENV_CONSUL_ROLE = "consul.role";
    String ENV_CONSUL_CLUSTER_MEMBER = "consul.cluster.member";

    public static void main(String[] args) {
        JulToSlf4jBridge.setup();
        new Launcher().start();
    }

    private void start() {
        AppMaker appMaker = new AppMaker();

        EC2 ec2 = EC2Factory.get();

        String nodeName = ec2.isEc2() ? ec2.getTagValue(ENV_NODE_NAME) : "consul";

        List<String> clusterMembers = ec2.isEc2() ? ec2.getInstanceIPByTagValue(ENV_CONSUL_ROLE, "server") : Env.getList(ENV_CONSUL_CLUSTER_MEMBER, " ");

        ConsulCommandBuilder serverCommand = ConsulCommandBuilder.server(true, clusterMembers);

        App app = appMaker.makeApp(nodeName, serverCommand, "host", getEnv());

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        app.start(Environment.get().forceRestart());
    }

    public List<String> getEnv() {
        List<String> env = new ArrayList<>();
        env.add("CONSUL_BIND_INTERFACE=eth0");
        return env;
    }


}
