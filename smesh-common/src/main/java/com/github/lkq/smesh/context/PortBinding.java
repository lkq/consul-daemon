package com.github.lkq.smesh.context;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;

public class PortBinding {
    private int hostPort;
    private int containerPort;
    private Protocol protocol;

    public enum Protocol {TCP, UDP}

    public PortBinding(int hostPort, int containerPort, Protocol protocol) {
        this.hostPort = hostPort;
        this.containerPort = containerPort;
        this.protocol = protocol;
    }

    public PortBinding(int port, Protocol protocol) {
        this.hostPort = port;
        this.containerPort = port;
        this.protocol = protocol;
    }

    public ExposedPort getExposedPort() {
        switch (protocol) {
            case UDP:
                return ExposedPort.udp(containerPort);
            default:
                return ExposedPort.tcp(containerPort);
        }
    }

    public Ports.Binding getPortBinding() {
        return Ports.Binding.bindPort(hostPort);
    }

    @Override
    public String toString() {
        return "PortBinding{" +
                "hostPort=" + hostPort +
                ", containerPort=" + containerPort +
                ", protocol=" + protocol +
                '}';
    }
}
