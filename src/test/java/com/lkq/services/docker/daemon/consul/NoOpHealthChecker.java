package com.lkq.services.docker.daemon.consul;

public class NoOpHealthChecker extends ConsulHealthChecker {
    public NoOpHealthChecker() {
        super(null, null, null);
    }

    @Override
    public boolean isHealthy() {
        return false;
    }
}
