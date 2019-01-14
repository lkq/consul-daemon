package com.github.lkq.smesh.test;

import com.github.lkq.smesh.test.consul.ConsulMainLocal;
import com.github.lkq.smesh.test.linkerd.LinkerdMainLocal;
import com.github.lkq.smesh.test.userapp.UserAppLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestEngine {
    public static final String USER_APP = "userapp";
    public static final int REG_PORT = 1025;
    public static final int LINKERD_PORT = 1026;

    private static Logger logger = LoggerFactory.getLogger(TestEngine.class);

    private static Boolean started = false;

    private static TestEngine instance = new TestEngine();
    private String consulContainerId;
    private UserAppLocal userApp;

    public static TestEngine get() {
        return instance;
    }

    private TestEngine(){}

    public synchronized void startEverything() {
        if (!started) {
            consulContainerId = startConsul(REG_PORT);
            startLinerd(LINKERD_PORT, consulContainerId);
            userApp = startUserApp(8081, REG_PORT);
            started = true;
        } else {
            logger.info("test engine already started");
        }
    }

    public void stopEverything() {
        stopConsul();
        stopLinkerd();
        stopUserApp();
    }

    /**
     * start a local consul docker container
     * @return container id
     * @param port
     */
    public String startConsul(int port) {
        return ConsulMainLocal.start(port);
    }

    public void stopConsul() {
        ConsulMainLocal.stop();
    }

    /**
     * start a local linkerd docker container with binding port 8080 (due to unable to use host network in mac)
     * @return container id
     * @param port
     */
    public String startLinerd(int port, String consulContainer) {
        return LinkerdMainLocal.start(port, consulContainer);
    }

    private void stopLinkerd() {
        LinkerdMainLocal.stop();
    }

    /**
     * build a docker image containing UserApp and start it
     * @return container id
     * @param servicePort
     * @param regPort
     */
    public UserAppLocal startUserApp(int servicePort, int regPort) {
        UserAppLocal userApp = new UserAppLocal();
        userApp.start(servicePort, "ws://localhost:" + regPort + "/smesh/register/v1");
        return userApp;
    }

    public void stopUserApp() {
        if (userApp != null) {
            userApp.stop();
        }
    }
}
