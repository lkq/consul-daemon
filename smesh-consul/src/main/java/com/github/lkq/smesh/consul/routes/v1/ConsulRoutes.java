package com.github.lkq.smesh.consul.routes.v1;

import com.github.lkq.smesh.consul.handler.AppInfo;
import com.github.lkq.smesh.server.Routes;
import spark.Service;

public class ConsulRoutes implements Routes {

    private AppInfo appInfo;

    public ConsulRoutes(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    @Override
    public void ignite(Service service) {
        service.get("/", appInfo::getAppInfo);
    }
}
