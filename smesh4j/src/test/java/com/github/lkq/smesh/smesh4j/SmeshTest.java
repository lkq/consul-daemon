package com.github.lkq.smesh.smesh4j;

import org.junit.jupiter.api.Test;

class SmeshTest {

    @Test
    void canRegisterService() {
        Smesh smesh = new Smesh();
        smesh.register("abc");
    }
}