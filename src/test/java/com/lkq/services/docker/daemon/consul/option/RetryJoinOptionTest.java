package com.lkq.services.docker.daemon.consul.option;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.MockitoAnnotations.initMocks;

class RetryJoinOptionTest {

    private RetryJoinOption retryJoinOptionAWS;

    @BeforeEach
    void setUp() {
        initMocks(this);
        retryJoinOptionAWS = new RetryJoinOption("127.0.0.1 127.0.0.2 127.0.0.3");
    }

    @Test
    void canBuildRetryJoinOption() {
        String options = retryJoinOptionAWS.build();

        assertThat(options, is(" -retry-join 127.0.0.1 -retry-join 127.0.0.2 -retry-join 127.0.0.3"));
    }
}