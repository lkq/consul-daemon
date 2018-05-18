package com.github.lkq.smesh.smesh4j;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

class SmeshTest {

    @Test
    void canRegisterService() throws URISyntaxException {
        Smesh smesh = new Smesh(new URI("ws://localhost:8080/register"));
        smesh.register("abc");
    }
}