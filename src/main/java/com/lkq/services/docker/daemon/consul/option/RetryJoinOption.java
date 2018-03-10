package com.lkq.services.docker.daemon.consul.option;

import spark.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RetryJoinOption implements ConsulOption {

    private String host;

    public static List<RetryJoinOption> fromHosts(List<String> hosts) {
        return hosts.stream().filter(StringUtils::isNotEmpty).map(RetryJoinOption::new).collect(Collectors.toList());
    }

    public static List<RetryJoinOption> fromHosts(String hosts) {
        ArrayList<String> hostList = new ArrayList<>();
        String[] split = hosts.split(" ");
        for (String host : split) {
            if (StringUtils.isNotEmpty(host)) {
                hostList.add(host);
            }
        }
        return fromHosts(hostList);
    }

    public RetryJoinOption(String host) {
        this.host = host;
    }

    @Override
    public String build() {
        return "-retry-join=" + host;
    }
}
