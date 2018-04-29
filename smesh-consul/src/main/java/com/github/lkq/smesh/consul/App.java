package com.github.lkq.smesh.consul;

import com.github.lkq.smesh.server.WebServer;
import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.consul.health.ConsulHealthChecker;
import com.github.lkq.smesh.consul.api.ConsulController;
import com.github.lkq.smesh.exception.SmeshException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

public class App {
    private static Logger logger = LoggerFactory.getLogger(App.class);

    private final ContainerContext context;
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
    public App(ContainerContext context, ConsulController consulController, ConsulHealthChecker consulHealthChecker, WebServer webServer, String appVersion) {
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
                throw new SmeshException("failed to start consul instance, context: " + context);
            }
        } else {
            consulController.attachLogging(context.nodeName());
        }

        if (!consulHealthChecker.registerConsulDaemonVersion(appVersion, 10)) {
            throw new SmeshException("failed to register consul daemon version: " + appVersion);
        }

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
