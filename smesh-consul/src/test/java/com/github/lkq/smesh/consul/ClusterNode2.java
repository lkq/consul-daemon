package com.github.lkq.smesh.consul;

public class ClusterNode2 {
    public static void main(String[] args) {
        ClusterNode node = new ClusterNode();
        node.startNodes(2, null);
    }
}
