package com.github.lkq.smesh.consul.register;

import com.github.lkq.smesh.consul.client.ConsulClient;
import com.github.lkq.smesh.consul.client.http.Response;

public class ConsulRegistrar {
    private ConsulClient client;

    public ConsulRegistrar(ConsulClient client) {
        this.client = client;
    }

    public void deRegister() {

    }

    public String register(String service) {
        Response res = client.register(service);
        if (res.status() == 200) {
            return res.body();
        }
        return "";
    }
}
