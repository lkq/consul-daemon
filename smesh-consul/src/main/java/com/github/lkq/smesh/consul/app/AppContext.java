package com.github.lkq.smesh.consul.app;

import com.github.lkq.smesh.Env;
import com.github.lkq.smesh.consul.Constants;
import com.github.lkq.smesh.consul.Main;
import com.github.lkq.smesh.consul.aws.EC2;
import com.github.lkq.smesh.consul.aws.EC2Factory;
import com.github.lkq.smesh.consul.client.ConsulClient;
import com.github.lkq.smesh.consul.client.ResponseParser;
import com.github.lkq.smesh.consul.client.http.SimpleHttpClient;
import com.github.lkq.smesh.consul.config.Config;
import com.github.lkq.smesh.consul.config.ConsulContext;
import com.github.lkq.smesh.consul.exception.SmeshConsulException;
import com.github.lkq.smesh.profile.ProfileFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AppContext {

    public Config createConfig() {
        EC2 ec2 = EC2Factory.get();
        String nodeName = ec2.isEc2() ? ec2.getTagValue(Constants.ENV_NODE_NAME) : InetAddress.getLoopbackAddress().getHostName();
        List<String> clusterMembers = ec2.isEc2() ? ec2.getInstanceIPByTagValue(Constants.ENV_CONSUL_ROLE, "server") : Env.getList(Constants.ENV_CONSUL_CLUSTER_MEMBERS, ",");

        List<String> commands = Arrays.asList("agent", "-server");
        commands.addAll(clusterMembers.stream().map((value) -> "-retry-join=" + value).collect(Collectors.toList()));

        return new Config()
                .consulContext(new ConsulContext()
                        .imageName(Constants.CONSUL_IMAGE)
                        .hostName(nodeName)
                        .nodeName(nodeName)
                        .environmentVariables(Arrays.asList("CONSUL_BIND_INTERFACE=eth0"))
                        .portBindings(Arrays.asList())
                        .volumeBindings(Arrays.asList())
                        .commands(commands)
                );
    }

    public ProfileFactory createProfileFactory(String nodeName) {
        try {
            return new ProfileFactory(Main.class, nodeName);
        } catch (IOException e) {
            throw new SmeshConsulException("failed to create profile factory", e);
        }
    }

    public ConsulClient createConsulClient() {
        return new ConsulClient(new SimpleHttpClient(), new ResponseParser(), Constants.CONSUL_URL);
    }

    public Logger createContainerLogger() {
        return LoggerFactory.getLogger("smesh-consul");
    }
}
