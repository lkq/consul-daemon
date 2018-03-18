package com.lkq.services.docker.daemon.health;

import com.lkq.services.docker.daemon.consul.ConsulAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaemonVersionRegister {
    private static Logger logger = LoggerFactory.getLogger(DaemonVersionRegister.class);

    private final ConsulAPI consulAPI;
    private final String daemonVersionKey;
    private final String daemonVersion;

    public DaemonVersionRegister(ConsulAPI consulAPI, String nodeName, String daemonVersion) {
        this.consulAPI = consulAPI;
        this.daemonVersionKey = nodeName + "-daemon-version";
        this.daemonVersion = daemonVersion;
    }

    public String getDaemonVersion() {
        return consulAPI.getKeyValue(daemonVersionKey);
    }

    public boolean registerDaemonVersion() {
        return consulAPI.putKeyValue(daemonVersionKey, daemonVersion + "@" + System.currentTimeMillis());
    }
}
