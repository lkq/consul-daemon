package com.github.lkq.smesh.smesh4j;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class Smesh {
    private static final String CONSUL_AGENT_URL = "http://localhost:8500";
    private static final String DEREGISTRATION_URL = "/v1/agent/service/deregister/";
    private static final String REGISTRATION_URL = "/v1/agent/service/register";
    private static final String KEY_VALUE_URL = "/v1/kv/";
    public void register() throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url("").build();

        Response response = client.newCall(request).execute();
    }
}
