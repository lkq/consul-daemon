package com.github.lkq.smesh.consul;

import com.github.lkq.smesh.consul.client.ConsulClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class VersionRegisterTest {

    public static final String TEST_KEY = "test-key";
    public static final String EXPECTED_VERSION = "1.2.3";
    public static final String CURRENT_VERSION = "1.2.0";
    private VersionRegister versionRegister;
    @Mock
    private ConsulClient consulAPI;

    @BeforeEach
    void setUp() {
        initMocks(this);
        versionRegister = new VersionRegister(consulAPI, TEST_KEY, EXPECTED_VERSION, 10);
    }

    @Test
    void canRegisterVersion() {
        given(consulAPI.getKeyValue(TEST_KEY)).willReturn(CURRENT_VERSION).willReturn(EXPECTED_VERSION);
        versionRegister.registerVersion();
        delay(30);

        verify(consulAPI, times(1)).putKeyValue(TEST_KEY, EXPECTED_VERSION);
    }

    @Test
    void willExistIfVersionAlreadyRegistered() {
        given(consulAPI.getKeyValue(TEST_KEY)).willReturn(EXPECTED_VERSION);
        versionRegister.registerVersion();
        delay(30);

        verify(consulAPI, never()).putKeyValue(anyString(), anyString());
    }

    @Test
    void canRegisterVersionAfterRetry() {
        given(consulAPI.getKeyValue(TEST_KEY)).willReturn(CURRENT_VERSION).willReturn(CURRENT_VERSION).willReturn(EXPECTED_VERSION);
        versionRegister.registerVersion();
        delay(50);

        verify(consulAPI, times(2)).putKeyValue(TEST_KEY, EXPECTED_VERSION);
    }

    private void delay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception ignored) {

        }
    }
}