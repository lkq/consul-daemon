package com.github.lkq.smesh;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TimestampTest {
    private static Logger logger = LoggerFactory.getLogger(TimestampTest.class);
    @Test
    void canGetTimestamp() {
        String timestamp = Timestamp.get();
        logger.info("timestamp: {}", timestamp);
        assertTrue(timestamp.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+"));
    }

    @Test
    void canGetUpTime() throws InterruptedException {
        String upTime = Timestamp.upTime();
        Thread.sleep(10);
        logger.info("upTime: {}", upTime);
        assertTrue(upTime.matches("\\d+d \\d+:\\d+"));
    }
}