package com.github.lkq.smesh.consul;

import com.github.lkq.smesh.consul.cluster.AppContextCluster;

public class ClusterNode1 {
    public static void main(String[] args) {
        ClusterNode node = new ClusterNode();
        node.startNode(1027, AppContextCluster.node1());
    }
}
