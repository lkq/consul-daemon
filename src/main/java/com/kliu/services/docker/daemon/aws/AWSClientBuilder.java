package com.kliu.services.docker.daemon.aws;

import com.amazonaws.util.EC2MetadataUtils;
import spark.utils.StringUtils;

public class AWSClientBuilder {
    public AWSClient build() {

        boolean hasInstanceID = false;
        try {
            hasInstanceID = StringUtils.isNotEmpty(EC2MetadataUtils.getInstanceId());
        } catch (Throwable ignored) {
        }

        if (hasInstanceID) {
            return new AWSClientImpl();
        } else {
            return new AWSClientStub();
        }
    }
}
