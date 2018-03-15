package com.lkq.services.docker.daemon;

import com.lkq.services.docker.daemon.container.PortBinder;

import java.util.ArrayList;
import java.util.List;

public class ConsulPorts {

    public List<PortBinder> getPortBinders() {
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
}
