package com.lkq.services.docker.daemon.consul.option;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.MockitoAnnotations.initMocks;

class RetryJoinOptionTest {

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void canBuildRetryJoinOptionWithHost() {
        RetryJoinOption retryJoinOptionAWS = new RetryJoinOption("127.0.0.1");
        String options = retryJoinOptionAWS.build();

        assertThat(options, is("-retry-join=127.0.0.1"));
    }

    @Test
    void canBuildRetryJoinOption() {
        List<RetryJoinOption> options = RetryJoinOption.fromHosts(Arrays.asList("127.0.0.1", "127.0.0.2", "127.0.0.3"));

        assertThat(options.size(), is(3));

        assertThat(options.get(0).build(), is("-retry-join=127.0.0.1"));
        assertThat(options.get(1).build(), is("-retry-join=127.0.0.2"));
        assertThat(options.get(2).build(), is("-retry-join=127.0.0.3"));
    }
}