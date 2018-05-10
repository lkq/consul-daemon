package com.github.lkq.smesh.test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class SmeshIntegrationTest {

    private static TestEngine testEngine = new TestEngine();

    @BeforeAll
    static void setUp() throws IOException, InterruptedException {
        testEngine.startEverything();
    }

    @Test
    void canRouteToTheCorrectServiceViaSmesh() {

    }
}
