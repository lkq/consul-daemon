package com.lkq.services.docker.daemon;

import com.lkq.services.docker.daemon.consul.ConsulController;
import com.lkq.services.docker.daemon.consul.context.ConsulContext;
import com.lkq.services.docker.daemon.exception.ConsulDaemonException;
import com.lkq.services.docker.daemon.health.ConsulHealthChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

public class App {
    private static Logger logger = LoggerFactory.getLogger(App.class);

    private final ConsulContext context;
    private final String appVersion;
    private final ConsulController consulController;
    private final ConsulHealthChecker consulHealthChecker;
    private final WebServer webServer;

    /**
     * application entry point, a place to put together different pieces and make it run
     *
     * @param context
     * @param consulController
     * @param consulHealthChecker
     * @param webServer
     * @param appVersion
     */
    public App(ConsulContext context, ConsulController consulController, ConsulHealthChecker consulHealthChecker, WebServer webServer, String appVersion) {
        this.context = context;
        this.appVersion = appVersion;
        this.consulHealthChecker = consulHealthChecker;
        this.consulController = consulController;
        this.webServer = webServer;
    }

    /**
     * start the application
     *
     * @param cleanStart weather should do a clean start or not,
     *                   if not specified, will clean start if the registered consul daemon version does not match with current package version.
     */
    public void start(Boolean cleanStart) {

        String registeredVersion = consulHealthChecker.registeredConsulDaemonVersion();

        cleanStart = shouldCleanStart(appVersion, registeredVersion, cleanStart);
        logger.info("old version: {}, new version: {}, force restart: {}", registeredVersion, appVersion, cleanStart);
        if (cleanStart) {
            consulController.stopAndRemoveExistingInstance(context.nodeName());
            if (consulController.startNewInstance(context)) {
                consulController.attachLogging(context.nodeName());
            } else {
                throw new ConsulDaemonException("failed to start consul instance, context: " + context);
            }
        } else {
            consulController.attachLogging(context.nodeName());
        }
        // TODO: find a better solution to block until consul started
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        consulHealthChecker.registerConsulDaemonVersion(appVersion);

        webServer.start();

    }

    private boolean shouldCleanStart(String appVersion, String registeredVersion, Boolean cleanStart) {
        if (cleanStart == null) {
            if (StringUtils.isNotEmpty(registeredVersion)) {
                String version = registeredVersion.split("@")[0];
                if (version.equals(appVersion)) {
                    return false;
                }
            }
            return true;
        } else {
            return cleanStart;
        }
    }

    public void stop() {
        consulController.stop(context.nodeName());
        webServer.stop();
    }

}
