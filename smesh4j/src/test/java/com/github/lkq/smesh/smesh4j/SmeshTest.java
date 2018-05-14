package com.github.lkq.smesh.smesh4j;

import org.junit.jupiter.api.Test;

import java.util.UUID;

class SmeshTest {

    @Test
    void canRegisterService() {
        Smesh smesh = new Smesh();
        String service = new Service()
                .withID(UUID.randomUUID().toString())
                .withName("mock-service")
                .withPort(1234)
                .withTags("mock")
                .withAddress("127.0.0.1")
                .build();

        smesh.register(service);
    }
}