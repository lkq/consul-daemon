package com.kliu.services.docker.daemon.consul;

import com.kliu.services.docker.daemon.aws.AWSClient;
import com.kliu.services.docker.daemon.aws.AWSClientBuilder;
import com.kliu.services.docker.daemon.consul.option.Bootstrap;
import com.kliu.services.docker.daemon.container.PortBinder;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ConsulContext {
    private static final String NET_EASY_HUB = "http://hub-mirror.c.163.com";
    private final AWSClient awsClient;

    public ConsulContext() {
        awsClient = new AWSClientBuilder().build();
    }

    public String getConfigPath() {
        return Paths.get(".").toAbsolutePath().normalize().toString() + "/config";
    }

    public String getDataPath() {
        return Paths.get(".").toAbsolutePath().normalize().toString() + "/data";
    }

    public String getNetwork() {
        return "host";
    }

    public String getContainerName() {
        return "consul";
    }

    public String getImageName() {
        return "consul:1.0.6";
    }

    public String[] getConsulCommands() {
        return new ConsulCommandBuilder()
                .with("agent")
                .with("-server")
                .with("-ui")
                .with(new Bootstrap())
//                .with(new BootstrapCount(3))
                .build();
    }

    public List<PortBinder> getPortBinders() {
        List<PortBinder> portBinders = new ArrayList<>();
        if (!"host".equals(getNetwork())) {
            portBinders.add(new PortBinder(8300, PortBinder.Protocol.TCP));
            portBinders.add(new PortBinder(8301, PortBinder.Protocol.TCP));
            portBinders.add(new PortBinder(8302, PortBinder.Protocol.TCP));
            portBinders.add(new PortBinder(8400, PortBinder.Protocol.TCP));
            portBinders.add(new PortBinder(8500, PortBinder.Protocol.TCP));
            portBinders.add(new PortBinder(8301, PortBinder.Protocol.UDP));
            portBinders.add(new PortBinder(8302, PortBinder.Protocol.UDP));
        }
        return portBinders;
    }

    public List<String> getEnvironmentVariables() {
        List<String> env = new ArrayList<>();
        env.add("CONSUL_BIND_INTERFACE=eth0");
        return env;
    }
}
