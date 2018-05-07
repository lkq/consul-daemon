package com.github.lkq.smesh.consul;

public class ClusterNode0 {
    public static void main(String[] args) {
        ClusterNode node = new ClusterNode();
        node.startNodes(0, ConsulPortBindings.defaultBindings());
    }
}
