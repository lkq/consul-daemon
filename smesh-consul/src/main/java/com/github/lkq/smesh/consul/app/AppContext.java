package com.github.lkq.smesh.consul.app;

import com.github.lkq.smesh.consul.Constants;
import com.github.lkq.smesh.consul.aws.EC2;
import com.github.lkq.smesh.consul.aws.EC2Factory;
import com.github.lkq.smesh.consul.client.ConsulClient;
import com.github.lkq.smesh.consul.client.ResponseParser;
import com.github.lkq.smesh.consul.client.http.SimpleHttpClient;
import com.github.lkq.smesh.consul.config.Config;
import com.github.lkq.smesh.consul.config.ConsulContext;
import com.github.lkq.smesh.consul.exception.SmeshConsulException;
import com.github.lkq.smesh.consul.profile.ProfileFactory;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import sun.rmi.rmic.Main;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;

public class AppContext {

    public Config createConfig() {
        EC2 ec2 = EC2Factory.get();
        String nodeName = ec2.isEc2() ? ec2.getTagValue(Constants.ENV_NODE_NAME) : InetAddress.getLoopbackAddress().getHostName();
        return new Config()
                .consulContext(new ConsulContext()
                        .imageName("consul:1.0.6")
                        .hostName("localhost")
                        .nodeName(nodeName)
                        .environmentVariables(Arrays.asList("CONSUL_BIND_INTERFACE=eth0"))
                        .portBindings(Arrays.asList())
                        .volumeBindings(Arrays.asList())
                        .commands(Arrays.asList("agent", "-server"))
                );
    }

    public ProfileFactory createProfileFactory(String nodeName) {
        try {
            return new ProfileFactory(Main.class, nodeName);
        } catch (IOException e) {
            throw new SmeshConsulException("failed to create profile factory", e);
        }
    }

    public SimpleDockerClient createDockerClient() {
        return SimpleDockerClient.create();
    }

    public ConsulClient createConsulClient() {
        return new ConsulClient(new SimpleHttpClient(), new ResponseParser(), Constants.CONSUL_URL);
    }
}
