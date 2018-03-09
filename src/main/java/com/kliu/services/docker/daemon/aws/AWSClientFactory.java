package com.kliu.services.docker.daemon.aws;

import com.amazonaws.util.EC2MetadataUtils;
import spark.utils.StringUtils;

public class AWSClientFactory {
    private static AWSClient instance;

    public synchronized static AWSClient get() {
        if (instance == null) {
            instance = create();
        }
        return instance;
    }

    private static AWSClient create() {
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
