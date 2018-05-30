package com.github.lkq.smesh.consul.register;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;

class ResponseFactoryTest {

    private ResponseFactory responseFactory;

    @BeforeEach
    void setUp() {
        responseFactory = new ResponseFactory();
    }

    @Test
    void canResponseSuccess() {
        String res = responseFactory.responseSuccess("test-message");
        assertThat(res, CoreMatchers.is("{\"status\":\"success\",\"message\":\"test-message\"}"));
    }

    @Test
    void canResponseFail() {
        String res = responseFactory.responseFail("fail-reason", "test-message");
        assertThat(res, CoreMatchers.is("{\"status\":\"fail\",\"reason\":\"fail-reason\",\"message\":\"test-message\"}"));
    }
}