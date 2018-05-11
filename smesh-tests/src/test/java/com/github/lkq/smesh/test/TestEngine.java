package com.github.lkq.smesh.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TestEngine {

    private static Logger logger = LoggerFactory.getLogger(TestEngine.class);

    private final UserAppPackager packager = new UserAppPackager();

    public void startEverything() throws IOException, InterruptedException {
        String[] artifact = packager.buildPackage();
        logger.info("test server build success: {}/{}", artifact[0], artifact[1]);
    }

}
