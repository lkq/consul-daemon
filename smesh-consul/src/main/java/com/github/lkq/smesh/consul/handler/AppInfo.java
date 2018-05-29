package com.github.lkq.smesh.consul.handler;

import com.github.lkq.smesh.AppVersion;
import com.github.lkq.smesh.Timestamp;
import com.github.lkq.smesh.consul.App;
import com.github.lkq.smesh.consul.Constants;
import com.github.lkq.smesh.consul.client.ConsulClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * check the local consul instance healthiness by putting a kv pair and query back
 */
public class AppInfo {

    private final ConsulClient consulClient;
    private final String nodeName;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private Map<String, Object> appInfo = new HashMap<>();

    public AppInfo(ConsulClient consulClient, String nodeName) {
        this.consulClient = consulClient;
        this.nodeName = nodeName;
        enrichAppInfo(appInfo);
    }

    private void enrichAppInfo(Map<String, Object> appInfo) {
        appInfo.put("name", Constants.APP_NAME);
        appInfo.put("version", AppVersion.get(App.class));
        appInfo.put("available", true);
        appInfo.put("consul", consulClient.getNodeHealth(nodeName));
        appInfo.put("timestamp", Timestamp.get());
        appInfo.put("uptime", Timestamp.upTime());
    }

    public String getAppInfo(Request request, Response response) {
        enrichAppInfo(appInfo);
        return gson.toJson(appInfo);
    }
}
