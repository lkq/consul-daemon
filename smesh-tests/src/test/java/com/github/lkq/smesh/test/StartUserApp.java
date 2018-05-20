package com.github.lkq.smesh.test;

public class StartUserApp {
    public static void main(String[] args) {
        new TestEngine().startUserApp(8081, "ws://172.17.0.2:1025/register");


        try {
            Thread.sleep(3000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
