package com.github.lkq.smesh.docker;

import com.github.lkq.smesh.context.PortBinding;

import java.util.Arrays;
import java.util.List;

public class ContainerNetwork {
    private String network;
    private List<PortBinding> portBindings;

    public static ContainerNetwork LINKERD_MAC = new ContainerNetwork("", Arrays.asList(new PortBinding(8080), new PortBinding(9990)));
    public static ContainerNetwork LINKERD_SERVER = new ContainerNetwork("host", null);
    public static ContainerNetwork CONSUL_SERVER = new ContainerNetwork("host", null);
    public static ContainerNetwork CONSUL_MAC = new ContainerNetwork("", Arrays.asList(
            new PortBinding(8300),
            new PortBinding(8301),
            new PortBinding(8302),
            new PortBinding(8400),
            new PortBinding(8500),
            new PortBinding(8301, PortBinding.Protocol.UDP),
            new PortBinding(8302, PortBinding.Protocol.UDP)
    ));

    public ContainerNetwork(String network, List<PortBinding> portBindings) {
        this.network = network;
        this.portBindings = portBindings;
    }

    public String network() {
        return network;
    }

    public List<PortBinding> portBindings() {
        return portBindings;
    }
}
