package com.kliu.services.docker.daemon.aws;

import com.amazonaws.util.EC2MetadataUtils;

public class AWSClientImpl implements AWSClient {

    AWSClientImpl() {}

    public boolean isAws() {
        return true;
    }

    public String getPrivateIP() {
        return EC2MetadataUtils.getPrivateIpAddress();
    }
}
