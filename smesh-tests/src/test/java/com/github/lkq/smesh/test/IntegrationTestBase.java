package com.github.lkq.smesh.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;

public class IntegrationTestBase {

    @BeforeAll
    static void setUp() throws IOException, InterruptedException {
        JulToSlf4jBridge.setup();
        TestEngine.get().startEverything();
    }

    @AfterAll
    static void tearDown() {
        TestEngine.get().stopEverything();
    }

}
