package com.kliu.services.docker.daemon.consul.option;

public class BootstrapCount implements OptionBuilder {

    private int count;

    public BootstrapCount(int count) {
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
