package com.github.lkq.smesh.test;

import java.util.Random;

public class UserService {

    private final Random random = new Random(System.nanoTime());

    public String getUser(String name) {
        return "{\"name\": " + name + ", \"lucky-score\":" + random.nextInt(100) + "}";
    }
}
