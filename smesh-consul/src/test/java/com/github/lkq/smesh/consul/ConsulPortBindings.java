package com.github.lkq.smesh.consul;

import com.github.lkq.smesh.context.PortBinding;

import java.util.ArrayList;
import java.util.List;

public class ConsulPortBindings {

    public static List<PortBinding> defaultBindings() {
        List<PortBinding> portBindings = new ArrayList<>();
        portBindings.add(new PortBinding(8300, PortBinding.Protocol.TCP));
        portBindings.add(new PortBinding(8301, PortBinding.Protocol.TCP));
        portBindings.add(new PortBinding(8302, PortBinding.Protocol.TCP));
        portBindings.add(new PortBinding(8400, PortBinding.Protocol.TCP));
        portBindings.add(new PortBinding(8500, PortBinding.Protocol.TCP));
        portBindings.add(new PortBinding(8301, PortBinding.Protocol.UDP));
        portBindings.add(new PortBinding(8302, PortBinding.Protocol.UDP));
        return portBindings;
    }

    public static List<PortBinding> localServerBindings() {
        List<PortBinding> portBindings = new ArrayList<>();
        portBindings.add(new PortBinding(9300, 8300, PortBinding.Protocol.TCP));
        portBindings.add(new PortBinding(9301, 8301, PortBinding.Protocol.TCP));
        portBindings.add(new PortBinding(9302, 8302, PortBinding.Protocol.TCP));
        portBindings.add(new PortBinding(9400, 8400, PortBinding.Protocol.TCP));
        portBindings.add(new PortBinding(9500, 8500, PortBinding.Protocol.TCP));
        portBindings.add(new PortBinding(9301, 8301, PortBinding.Protocol.UDP));
        portBindings.add(new PortBinding(9302, 8302, PortBinding.Protocol.UDP));
        return portBindings;
    }

    public static List<PortBinding> localClientBindings() {
        List<PortBinding> portBindings = new ArrayList<>();
        portBindings.add(new PortBinding(7300, 8300, PortBinding.Protocol.TCP));
        portBindings.add(new PortBinding(7301, 8301, PortBinding.Protocol.TCP));
        portBindings.add(new PortBinding(7302, 8302, PortBinding.Protocol.TCP));
        portBindings.add(new PortBinding(7400, 8400, PortBinding.Protocol.TCP));
        portBindings.add(new PortBinding(7500, 8500, PortBinding.Protocol.TCP));
        portBindings.add(new PortBinding(7301, 8301, PortBinding.Protocol.UDP));
        portBindings.add(new PortBinding(7302, 8302, PortBinding.Protocol.UDP));
        return portBindings;
    }
}
