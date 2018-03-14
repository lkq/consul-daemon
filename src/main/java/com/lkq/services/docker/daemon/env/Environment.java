package com.lkq.services.docker.daemon.env;

import java.util.List;

public interface Environment {

    static Environment get() {
        return EnvironmentProvider.get();
    }

    enum ConsulRole {
        CLIENT,
        SERVER
    }

    ConsulRole consulRole();
    List<String> clusterMembers();
    String getDataPath();

//    public static String getEnv(String key, String defaultValue) {
//        String value = System.getenv(key);
//        if (StringUtils.isEmpty(value)) {
//            value = System.getProperty(key, defaultValue);
//        }
//        if (StringUtils.isEmpty(value)) {
//            value = AWSClient.instance().getTagValue(key, defaultValue);
//        }
//        return value;
//    }
}
