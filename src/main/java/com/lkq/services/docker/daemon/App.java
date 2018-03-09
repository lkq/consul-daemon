package com.lkq.services.docker.daemon;

import com.lkq.services.docker.daemon.consul.ConsulController;
import com.lkq.services.docker.daemon.consul.ConsulHealthChecker;
import com.lkq.services.docker.daemon.consul.context.ConsulContext;
import com.lkq.services.docker.daemon.container.ContainerLogRedirector;
import com.lkq.services.docker.daemon.container.SimpleDockerClient;
import com.lkq.services.docker.daemon.handler.HealthCheckHandler;
import com.lkq.services.docker.daemon.routes.v1.Routes;

public class App {
    private final ConsulController consulController;
//docker run -d -h consul-node1 -p 8300:8300  -p 8301:8301  -p 8301:8301/udp  -p 8302:8302  -p 8302:8302/udp  -p 8400:8400  -p 8500:8500 consul:1.0.6 -server -advertise 192.168.43.73 -bootstrap-expect 3
//docker run --name=consul -d -p 8300-8302:8300-8302 -p 8301:8301/udp -p 8302:8302/udp -p 8400:8400 -p 8500:8500 -p 8600:53/udp -h node1 gliderlabs/consul-server:0.6 -server -bootstrap-expect 3 -ui -advertise 192.168.99.101 -join node1 -join node2 -join node3
//docker run --name=consul -d -p 8300-8302:8300-8302 -p 8301:8301/udp -p 8302:8302/udp -p 8400:8400 -p 8500:8500 -p 8600:53/udp -h node2 gliderlabs/consul-server:0.6 -server -ui -advertise 192.168.99.102 -join node1 -join node2 -join node3
//docker run --name=consul -d -p 8300-8302:8300-8302 -p 8301:8301/udp -p 8302:8302/udp -p 8400:8400 -p 8500:8500 -p 8600:53/udp -h node3 gliderlabs/consul-server:0.6 -server -ui -advertise 192.168.99.103 -join node1 -join node2 -join node3

    public App() {
        consulController = new ConsulController(
                new SimpleDockerClient(),
                new ConsulHealthChecker(),
                new ContainerLogRedirector());
    }

    public void start(ConsulContext context) {

        consulController.start(context);

        new Routes(new HealthCheckHandler(consulController)).ignite();

    }
}
