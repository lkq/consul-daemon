package com.lkq.services.docker.daemon.utils;

import com.github.lkq.smesh.exception.ConsulDaemonException;
import org.eclipse.jetty.client.HttpClient;

public class HttpClientFactory {
    public HttpClient create() {
        HttpClient httpClient = new HttpClient();
        try {
            httpClient.start();
            return httpClient;
        } catch (Exception e) {
            throw new ConsulDaemonException("failed to start http client", e);
        }
    }
}
