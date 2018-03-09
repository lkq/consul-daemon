package com.lkq.services.docker.daemon.consul.option;

public class BootstrapExpectOption implements ConsulOption {

    private int count;

    public BootstrapExpectOption(int count) {
        this.count = count;
    }

    @Override
    public String build() {
        if (count > 0) {
            return "-bootstrap-expect=" + count;
        } else {
            return "";
        }
    }
}
