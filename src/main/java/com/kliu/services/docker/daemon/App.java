package com.kliu.services.docker.daemon;

import com.kliu.services.docker.daemon.consul.ConsulDockerController;
import com.kliu.services.docker.daemon.handler.HealthCheckHandler;
import com.kliu.services.docker.daemon.routes.v1.Routes;

public class App {

    //docker run --name=consul -d -p 8300-8302:8300-8302 -p 8301:8301/udp -p 8302:8302/udp -p 8400:8400 -p 8500:8500 -p 8600:53/udp -h node1 gliderlabs/consul-server:0.6 -server -bootstrap-expect 3 -ui -advertise 192.168.99.101 -join node1 -join node2 -join node3
//docker run --name=consul -d -p 8300-8302:8300-8302 -p 8301:8301/udp -p 8302:8302/udp -p 8400:8400 -p 8500:8500 -p 8600:53/udp -h node2 gliderlabs/consul-server:0.6 -server -ui -advertise 192.168.99.102 -join node1 -join node2 -join node3
//docker run --name=consul -d -p 8300-8302:8300-8302 -p 8301:8301/udp -p 8302:8302/udp -p 8400:8400 -p 8500:8500 -p 8600:53/udp -h node3 gliderlabs/consul-server:0.6 -server -ui -advertise 192.168.99.103 -join node1 -join node2 -join node3
    public void start() {

        ConsulDockerController consulDockerController = new ConsulDockerController();

        new Routes(new HealthCheckHandler(consulDockerController)).ignite();

        consulDockerController.startConsul();
    }
}
