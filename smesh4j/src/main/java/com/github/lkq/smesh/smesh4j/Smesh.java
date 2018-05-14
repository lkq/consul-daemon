package com.github.lkq.smesh.smesh4j;

import okhttp3.*;

public class Smesh {
    private static final String BASE_URL = "http://localhost:8500";
    private static final String DEREG_URL = BASE_URL + "/v1/agent/service/deregister/";
    private static final String REG_URL = BASE_URL + "/v1/agent/service/register";
    private static final String KV_URL = BASE_URL + "/v1/kv/";
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    public void register(String service) {

        try {
            Request request = new Request.Builder()
                    .url(REG_URL)
                    .put(RequestBody.create(JSON, service))
                    .build();

            Response response = client.newCall(request).execute();
            System.out.println(service);
            System.out.println(response);
        } catch (Exception e) {
            throw new RuntimeException("failed to register service", e);
        }
    }
}
