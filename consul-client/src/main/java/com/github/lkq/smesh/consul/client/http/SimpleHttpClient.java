package com.github.lkq.smesh.consul.client.http;

import okhttp3.*;

public class SimpleHttpClient {
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient httpClient;

    public SimpleHttpClient() {
        httpClient = new OkHttpClient();
    }

    public Response get(String url) {
        Request request = new Request.Builder().url(url).build();
        return execute(request);
    }

    public Response put(String url, String body) {
        RequestBody content = RequestBody.create(JSON, body);
        Request request = new Request.Builder().put(content).url(url).build();
        return execute(request);
    }

    public Response post(String url, String body) {
        RequestBody content = RequestBody.create(JSON, body);
        Request request = new Request.Builder().url(url).post(content).build();
        return execute(request);
    }

    private Response execute(Request request) {
        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            return new Response(response.code(), response.body() != null ? response.body().string() : null);
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            throw new HttpException(e);
        }
    }
}
