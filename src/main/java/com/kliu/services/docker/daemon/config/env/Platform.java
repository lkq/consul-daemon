package com.kliu.services.docker.daemon.config.env;

import com.amazonaws.util.EC2MetadataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

public enum Platform {

    LINUX_AWS,
    LINUX,
    MAC,
    MAC_CLUSTER;

    private static Logger logger = LoggerFactory.getLogger(Platform.class);

    public static Platform get() {
        String osName = Environment.getEnv("os.name", null);
        logger.info("running platform: {}", osName);
        if (osName != null && !"".equals(osName.trim())) {
            if (osName.toLowerCase().startsWith("mac")) {
                if (Environment.getEnv("local.cluster", null) != null) {
                    return Platform.MAC_CLUSTER;
                }
                return Platform.MAC;
            }
            if (osName.toLowerCase().startsWith("linux")) {
                try {
                    boolean hasInstanceID = StringUtils.isNotEmpty(EC2MetadataUtils.getInstanceId());
                    if (hasInstanceID) {
                        return Platform.LINUX_AWS;
                    }
                } catch (Throwable ignored) {
                }
                return Platform.LINUX;
            }
        }
        throw new RuntimeException("unsupported platform: " + osName);
    }
}
