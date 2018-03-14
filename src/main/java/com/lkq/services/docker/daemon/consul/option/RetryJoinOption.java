package com.lkq.services.docker.daemon.consul.option;

import spark.utils.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class RetryJoinOption implements ConsulOption {

    private String host;

    public static List<RetryJoinOption> fromHosts(List<String> hosts) {
        return hosts.stream().filter(StringUtils::isNotEmpty).map(RetryJoinOption::new).collect(Collectors.toList());
    }

    public RetryJoinOption(String host) {
        this.host = host;
    }

    @Override
    public String build() {
        return "-retry-join=" + host;
    }
}
