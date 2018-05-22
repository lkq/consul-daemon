package com.github.lkq.smesh.docker;

import com.github.lkq.smesh.context.PortBinding;

import java.util.Arrays;
import java.util.List;

public class ContainerNetwork {
    private String network;
    private List<PortBinding> portBindings;

    public static ContainerNetwork LINKERD_MAC_LOCAL = new ContainerNetwork("", Arrays.asList(new PortBinding(8080), new PortBinding(9990)));
    public static ContainerNetwork LINKERD_SERVER = new ContainerNetwork("host", null);

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
