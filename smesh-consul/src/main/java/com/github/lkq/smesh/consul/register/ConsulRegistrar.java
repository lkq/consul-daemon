package com.github.lkq.smesh.consul.register;

import com.github.lkq.smesh.consul.client.ConsulClient;
import com.github.lkq.smesh.consul.client.http.Response;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ConsulRegistrar {
    private ConsulClient client;
    private String service;

    public ConsulRegistrar(ConsulClient client) {
        this.client = client;
    }

    public String deRegister() {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(service);
        String serviceId = jsonElement.getAsJsonObject().get("id").getAsString();
        Response res = client.deregister(serviceId);
        return res.body();
    }

    public String register(String service) {
        this.service = service;
        Response res = client.register(service);
        return res.body();
    }
}
