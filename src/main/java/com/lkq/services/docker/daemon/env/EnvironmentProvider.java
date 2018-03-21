package com.lkq.services.docker.daemon.env;

import com.amazonaws.util.EC2MetadataUtils;
import com.lkq.services.docker.daemon.exception.ConsulDaemonException;
import spark.utils.StringUtils;

public class EnvironmentProvider {

    private static Environment env;

    public synchronized static Environment get() {
        if (env == null) {
            if (StringUtils.isNotEmpty(EC2MetadataUtils.getInstanceId())) {
                env = new EC2Environment();
            } else {
                env = new LinuxEnvironment();
            }
        }
        return env;
    }

    public static void set(Environment env) {
        if (EnvironmentProvider.env != null) {
            throw new ConsulDaemonException("environment already initialized");
        }
        EnvironmentProvider.env = env;
    }

}
