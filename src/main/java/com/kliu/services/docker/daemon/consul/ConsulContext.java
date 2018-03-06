package com.kliu.services.docker.daemon.consul;

import com.kliu.services.docker.daemon.aws.AWSClient;
import com.kliu.services.docker.daemon.aws.AWSClientBuilder;
import com.kliu.services.docker.daemon.consul.option.BindAddress;
import com.kliu.services.docker.daemon.consul.option.Bootstrap;
import com.kliu.services.docker.daemon.container.PortBinder;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public Map<String, Object> getEnvironmentVariables() {
        return new EnvironmentVariableBuilder().build();
    }

    public String[] getStartCommands() {
        return new StartCommandBuilder()
                .with("agent")
                .with("-server")
                .with(new Bootstrap())
                .with(new BindAddress(awsClient))
//                .with(new BootstrapCount(3))
                .build();
    }

    public List<PortBinder> getPortBinders() {
        ArrayList<PortBinder> portBinders = new ArrayList<>();
        portBinders.add(new PortBinder(8300, PortBinder.Protocol.TCP));
        portBinders.add(new PortBinder(8301, PortBinder.Protocol.TCP));
        portBinders.add(new PortBinder(8302, PortBinder.Protocol.TCP));
        portBinders.add(new PortBinder(8400, PortBinder.Protocol.TCP));
        portBinders.add(new PortBinder(8500, PortBinder.Protocol.TCP));
        portBinders.add(new PortBinder(8301, PortBinder.Protocol.UDP));
        portBinders.add(new PortBinder(8302, PortBinder.Protocol.UDP));
        return portBinders;
    }
}
