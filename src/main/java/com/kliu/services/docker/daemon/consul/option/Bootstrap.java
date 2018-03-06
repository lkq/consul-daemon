package com.kliu.services.docker.daemon.consul.option;

public class Bootstrap implements OptionBuilder {
    @Override
    public String build() {
        return "-bootstrap";
    }
}
