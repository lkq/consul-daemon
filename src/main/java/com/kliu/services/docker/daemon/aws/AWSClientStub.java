package com.kliu.services.docker.daemon.aws;

public class AWSClientStub implements AWSClient {
    @Override
    public boolean isAws() {
        return false;
    }

    @Override
    public String getPrivateIP() {
        return null;
    }

    @Override
    public String getTag(String key, String defaultValue) {
        return defaultValue;
    }
}
