package com.github.lkq.smesh.consul.env.aws;

public class EC2Factory {
    private static Object lock = new Object();

    private static EC2 instance;

    public static EC2 get() {
        if (instance != null) {
            return instance;
        } else {
            synchronized (lock) {
                if (instance == null) {
                    try {
                        instance = new EC2Impl();
                    } catch (Exception e) {
                        instance = new NoOpEC2();
                    }
                }
            }
        }
        return instance;
    }
}
