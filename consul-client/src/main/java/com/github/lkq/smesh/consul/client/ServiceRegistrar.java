package com.github.lkq.smesh.consul.client;

import com.github.lkq.smesh.consul.client.http.Response;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * register or de-register a specific service
 */
public class ServiceRegistrar {
    private ConsulClient client;

    public ServiceRegistrar(ConsulClient client) {
        this.client = client;
    }

    public Response deRegister(String service) {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(service);
        String serviceId = jsonElement.getAsJsonObject().get("id").getAsString();
        return client.deregister(serviceId);
    }

    /**
     * it should be save to register multiple time without any side effect
     * @return response from consul
     * @param service
     */
    public Response register(String service) {
        return client.register(service);
    }
}
