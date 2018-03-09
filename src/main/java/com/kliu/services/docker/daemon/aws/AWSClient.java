package com.kliu.services.docker.daemon.aws;

public interface AWSClient {

    boolean isAws();

    String getPrivateIP();

    String getTag(String key, String defaultValue);
}
