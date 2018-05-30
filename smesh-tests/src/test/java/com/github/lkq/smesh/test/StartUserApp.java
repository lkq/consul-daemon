package com.github.lkq.smesh.test;

public class StartUserApp {
    public static void main(String[] args) {
        TestEngine.get().startUserApp(8081, "ws://localhost:1025/register/v1");

        try {
            Thread.sleep(3000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
