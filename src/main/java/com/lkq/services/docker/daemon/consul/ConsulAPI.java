package com.lkq.services.docker.daemon.consul;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsulAPI {
    private static Logger logger = LoggerFactory.getLogger(ConsulAPI.class);

    private static final String API_HOST = "http://localhost";

    private String API_V1;
    private String API_V1_KV;

    private final HttpClient httpClient;

    public ConsulAPI(HttpClient httpClient, int port) {
        this.httpClient = httpClient;
        this.API_V1 = API_HOST + ":" + port + "/v1/";
        this.API_V1_KV = API_V1 + "kv/";
    }

    public boolean putKeyValue(String key, String value) {
        try {
            ContentResponse response = httpClient.newRequest(API_V1_KV + key)
                    .method(HttpMethod.PUT)
                    .content(new StringContentProvider(value))
                    .send();
            if (response.getStatus() == 200) {
                return Boolean.valueOf(response.getContentAsString());
            }
        } catch (Exception e) {
            logger.error("failed to put kv: " + key + "=" + value, e);
        }
        return false;
    }

    public String getKeyValue(String key) {
        try {
            ContentResponse response = httpClient.GET(API_V1_KV + key);
            if (response.getStatus() == 200) {
                return response.getContentAsString();
            }
        } catch (Exception e) {
            logger.error("failed to get kv: " + key, e);
        }
        return null;
    }
}
