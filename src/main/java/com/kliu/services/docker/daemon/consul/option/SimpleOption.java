package com.kliu.services.docker.daemon.consul.option;

public class SimpleOption implements ConsulOption {
    private String value;

    public SimpleOption(String value) {
        this.value = value;
    }

    @Override
    public String build() {
        return value;
    }
}
