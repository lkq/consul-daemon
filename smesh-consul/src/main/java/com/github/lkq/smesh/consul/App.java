package com.github.lkq.smesh.consul;

import com.github.lkq.smesh.server.WebServer;
import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.consul.handler.AppInfo;
import com.github.lkq.smesh.consul.container.ConsulController;
import com.github.lkq.smesh.exception.SmeshException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

public class App {
    private static Logger logger = LoggerFactory.getLogger(App.class);

    private final ContainerContext context;
    private final ConsulController consulController;
    private final AppInfo appInfo;
    private final WebServer webServer;
    private final VersionRegister versionRegister;

    /**
     * application entry point, a place to put together different pieces and make it run
     *  @param context
     * @param consulController
     * @param appInfo
     * @param versionRegister
     * @param webServer
     * @param appVersion
     */
    public App(ContainerContext context, ConsulController consulController, AppInfo appInfo, VersionRegister versionRegister, WebServer webServer, String appVersion) {
        this.context = context;
        this.consulController = consulController;
        this.appInfo = appInfo;
        this.versionRegister = versionRegister;
        this.webServer = webServer;
    }

    /**
     * start the application
     *
     * @param cleanStart weather should do a clean start or not,
     *                   if not specified, will clean start if the registered consul daemon version does not match with current package version.
     */
    public String start(Boolean cleanStart) {
        String appVersion = versionRegister.expectedVersion();

        String registeredVersion = versionRegister.registeredVersion();

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

        versionRegister.registerVersion();

        webServer.start();

        return context.nodeName();
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
