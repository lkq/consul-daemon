package com.lkq.services.docker.daemon;

import com.github.lkq.smesh.docker.PortBinder;

import java.util.ArrayList;
import java.util.List;

public class ConsulPorts {

    public List<PortBinder> defaultPortBindings() {
        List<PortBinder> portBinders = new ArrayList<>();
        portBinders.add(new PortBinder(8300, PortBinder.Protocol.TCP));
        portBinders.add(new PortBinder(8301, PortBinder.Protocol.TCP));
        portBinders.add(new PortBinder(8302, PortBinder.Protocol.TCP));
        portBinders.add(new PortBinder(8400, PortBinder.Protocol.TCP));
        portBinders.add(new PortBinder(8500, PortBinder.Protocol.TCP));
        portBinders.add(new PortBinder(8301, PortBinder.Protocol.UDP));
        portBinders.add(new PortBinder(8302, PortBinder.Protocol.UDP));
        return portBinders;
    }

    public List<PortBinder> localServerPortBindings() {
        List<PortBinder> portBinders = new ArrayList<>();
        portBinders.add(new PortBinder(9300, 8300, PortBinder.Protocol.TCP));
        portBinders.add(new PortBinder(9301, 8301, PortBinder.Protocol.TCP));
        portBinders.add(new PortBinder(9302, 8302, PortBinder.Protocol.TCP));
        portBinders.add(new PortBinder(9400, 8400, PortBinder.Protocol.TCP));
        portBinders.add(new PortBinder(9500, 8500, PortBinder.Protocol.TCP));
        portBinders.add(new PortBinder(9301, 8301, PortBinder.Protocol.UDP));
        portBinders.add(new PortBinder(9302, 8302, PortBinder.Protocol.UDP));
        return portBinders;
    }

    public List<PortBinder> localClientPortBindings() {
        List<PortBinder> portBinders = new ArrayList<>();
        portBinders.add(new PortBinder(7300, 8300, PortBinder.Protocol.TCP));
        portBinders.add(new PortBinder(7301, 8301, PortBinder.Protocol.TCP));
        portBinders.add(new PortBinder(7302, 8302, PortBinder.Protocol.TCP));
        portBinders.add(new PortBinder(7400, 8400, PortBinder.Protocol.TCP));
        portBinders.add(new PortBinder(7500, 8500, PortBinder.Protocol.TCP));
        portBinders.add(new PortBinder(7301, 8301, PortBinder.Protocol.UDP));
        portBinders.add(new PortBinder(7302, 8302, PortBinder.Protocol.UDP));
        return portBinders;
    }
}
