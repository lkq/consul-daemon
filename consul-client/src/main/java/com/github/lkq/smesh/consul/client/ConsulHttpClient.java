package com.github.lkq.smesh.consul.client;

import okhttp3.*;

public class ConsulHttpClient {
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient httpClient;

    public ConsulHttpClient() {
        httpClient = new OkHttpClient();
    }

    public String get(String url) {
        Request request = new Request.Builder().url(url).build();
        return execute(request);
    }

    public String put(String url, String body) {
        RequestBody content = RequestBody.create(JSON, body);
        Request request = new Request.Builder().url(url).put(content).build();
        return execute(request);
    }

    public String post(String url, String body) {
        RequestBody content = RequestBody.create(JSON, body);
        Request request = new Request.Builder().url(url).post(content).build();
        return execute(request);
    }

    private String execute(Request request) {
        try {
            Response response = httpClient.newCall(request).execute();
            if (responseOK(response)) {
                ResponseBody responseBody = response.body();
                return responseBody != null ? responseBody.string() : response.message();
            }
            throw new HttpResponseException(response);
        } catch (HttpResponseException e) {
            throw e;
        } catch (Exception e) {
            throw new HttpResponseException(e);
        }
    }

    private boolean responseOK(Response response) {
        return response.code() == 200 || response.code() == 201;
    }
}
