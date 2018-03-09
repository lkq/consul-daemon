package com.kliu.services.docker.daemon.consul.option;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.MockitoAnnotations.initMocks;

class RetryJoinTest {

    private RetryJoin retryJoinAWS;

    @BeforeEach
    void setUp() {
        initMocks(this);
        retryJoinAWS = new RetryJoin("127.0.0.1 127.0.0.2 127.0.0.3");
    }

    @Test
    void canBuildRetryJoinOption() {
        String options = retryJoinAWS.build();

        assertThat(options, is(" -retry-join 127.0.0.1 -retry-join 127.0.0.2 -retry-join 127.0.0.3"));
    }
}