package com.github.lkq.smesh.consul.utils;

import com.github.lkq.smesh.exception.SmeshException;
import org.eclipse.jetty.client.HttpClient;

public class HttpClientFactory {
    public HttpClient create() {
        HttpClient httpClient = new HttpClient();
        try {
            httpClient.start();
            return httpClient;
        } catch (Exception e) {
            throw new SmeshException("failed to start http client", e);
        }
    }
}
