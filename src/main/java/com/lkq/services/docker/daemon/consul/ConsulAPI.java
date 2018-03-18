package com.lkq.services.docker.daemon.consul;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ConsulAPI {
    private static Logger logger = LoggerFactory.getLogger(ConsulAPI.class);

    private static final String API_HOST = "http://localhost";

    private String API_V1;
    private String API_V1_KV;

    private final HttpClient httpClient;
    private final ConsulResponseParser responseParser;

    public ConsulAPI(HttpClient httpClient, ConsulResponseParser responseParser, int port) {
        this.httpClient = httpClient;
        this.responseParser = responseParser;
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
            logger.error("failed to put kv: {}={}, cause={}", key, value, e.getMessage());
        }
        return false;
    }

    public String getKeyValue(String key) {
        try {
            String uri = API_V1_KV + key;
            logger.debug("getting key value from: {}", uri);
            ContentResponse response = httpClient.GET(uri);
            if (response.getStatus() == 200) {
                Map<String, String> kvMap = responseParser.parse(response.getContentAsString());
                return kvMap.get("Value");
            }
        } catch (Exception e) {
            logger.error("failed to get kv: {}, cause: {}", key, e.getMessage());
        }
        return null;
    }
}
