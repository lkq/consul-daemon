package com.lkq.services.docker.daemon.consul.option;

import spark.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class RetryJoinOption implements ConsulOption {

    private final List<String> clusterHosts;

    public RetryJoinOption(String hosts) {
        clusterHosts = getClusterHosts(hosts);
    }

    @Override
    public String build() {

        StringBuilder retryJoin = new StringBuilder();
        for (String host : clusterHosts) {
            if (StringUtils.isNotEmpty(host)) {
                retryJoin.append(" -retry-join ").append(host);
            }
        }
        return retryJoin.toString();
    }

    public int getHostCount() {
        return clusterHosts.size();
    }

    private List<String> getClusterHosts(String hosts) {
        String[] clusterHosts = hosts.split(" ");
        List<String> hostList = new ArrayList<>();
        for (String clusterHost : clusterHosts) {
            if (StringUtils.isNotEmpty(clusterHost.trim())) {
                hostList.add(clusterHost.trim());
            }
        }
        return hostList;
    }
}
