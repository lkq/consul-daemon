package com.github.lkq.smesh.consul;

public class ClusterNode1 {
    public static void main(String[] args) {
        ClusterNode node = new ClusterNode();
        node.startNodes(1, ConsulPortBindings.localServerBindings());
    }
}
