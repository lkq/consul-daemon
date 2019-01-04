package com.github.lkq.smesh.consul;

import com.github.lkq.smesh.consul.cluster.AppContextCluster;

public class ClusterNode2 {
    public static void main(String[] args) {
        ClusterNode node = new ClusterNode();
        node.startNode(1028, AppContextCluster.node2());
    }
}
