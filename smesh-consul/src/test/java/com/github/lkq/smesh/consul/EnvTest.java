package com.github.lkq.smesh.consul;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;


class EnvTest {

    public static final String TEST_KEY = "test.key";

    @Test
    void canGetListFromSystemProperties() {
        String values = "127.0.0.2 127.0.0.3 127.0.0.4";
        System.setProperty(TEST_KEY, values);
        List<String> list = Env.getList(TEST_KEY, " ");
        Assertions.assertLinesMatch(list, Arrays.asList("127.0.0.2", "127.0.0.3", "127.0.0.4"));
    }

    @Test
    void canGetFromSystemProperties() {
        String values = "127.0.0.2 127.0.0.3 127.0.0.4";
        System.setProperty(TEST_KEY, values);
        String value = Env.get(TEST_KEY);
        Assertions.assertEquals(values, value);
    }
}