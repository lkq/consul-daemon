package com.github.lkq.smesh.consul.controller;

import com.github.lkq.smesh.consul.client.ConsulClient;
import com.github.lkq.smesh.profile.Profile;
import com.github.lkq.smesh.profile.ProfileFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@ExtendWith(MockitoExtension.class)
class VersionControllerTest {

    public static final String ARTIFACT_NAME = "smesh-consul";
    public static final String NODE_NAME = "test-node";
    public static final String NEW_VERSION = "1.2.3";
    public static final String OLD_VERSION = "1.2.0";
    public static final String VERSION_KEY = ARTIFACT_NAME + "." + NODE_NAME;

    private VersionController versionRegister;
    @Mock
    private ConsulClient consulClient;
    @Mock
    private ProfileFactory profileFactory;
    @Mock
    private Profile profile;

    @BeforeEach
    void setUp() {
        initMocks(this);
        willReturn(profile).given(profileFactory).create();
        willReturn(ARTIFACT_NAME).given(profile).name();
        willReturn(NODE_NAME).given(profile).nodeName();
        willReturn(NEW_VERSION).given(profile).version();
        versionRegister = new VersionController(consulClient, profileFactory);
    }

    @Test
    void canRegisterVersion() throws InterruptedException {
        given(consulClient.getKeyValue(VERSION_KEY)).willReturn(OLD_VERSION).willReturn(NEW_VERSION);
        versionRegister.start();
        Thread.sleep(1500);

        verify(consulClient, times(2)).putKeyValue(VERSION_KEY, NEW_VERSION);
    }

    @Test
    void willExitIfVersionAlreadyRegistered() throws InterruptedException {
        given(consulClient.getKeyValue(VERSION_KEY)).willReturn(NEW_VERSION);
        versionRegister.start();
        Thread.sleep(50);

        verify(consulClient, times(1)).putKeyValue(VERSION_KEY, NEW_VERSION);
    }
}