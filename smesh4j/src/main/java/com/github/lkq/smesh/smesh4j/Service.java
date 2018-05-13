package com.github.lkq.smesh.smesh4j;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * reference: https://www.consul.io/docs/agent/services.html
 */
public class Service {

    private final Gson gson = new Gson();
    private Map<String, Object> service = new HashMap<>();

    public Service withID(String value) {
        service.put("id", value);
        return this;
    }
    public Service withName(String value) {
        service.put("name", value);
        return this;
    }
    public Service withTags(String... value) {
        service.put("tags", Arrays.asList(value));
        return this;
    }
    public Service withAddress(String value) {
        service.put("address", value);
        return this;
    }
    public Service withPort(String value) {
        service.put("port", value);
        return this;
    }

    /**
     * reference: https://www.consul.io/docs/agent/checks.html
     * @param url
     * @param interval
     * @return
     */
    public Service withHttpCheck(String url, String interval) {
        Map<String, Object> check = new HashMap<>();
        check.put("http", url);
        check.put("interval", interval);
        service.put("check", check);
        return this;
    }

    public String build() {
        return gson.toJson(service);
    }
}
