package com.lkq.services.docker.daemon.consul;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import java.util.Base64;
import java.util.Map;

public class ConsulAPI {
    private static Logger logger = LoggerFactory.getLogger(ConsulAPI.class);

    private static final String API_HOST = "http://localhost";

    private String API_V1;
    private String API_V1_KV;
    private String API_V1_HEALTH_NODE;

    private final HttpClient httpClient;
    private final ConsulResponseParser responseParser;

    public ConsulAPI(HttpClient httpClient, ConsulResponseParser responseParser, int port) {
        this.httpClient = httpClient;
        this.responseParser = responseParser;
        this.API_V1 = API_HOST + ":" + port + "/v1/";
        this.API_V1_KV = API_V1 + "kv/";
        this.API_V1_HEALTH_NODE = API_V1 + "health/node/";
    }

    public Map<String, String> getNodeHealth(String nodeName) {
        try {
            ContentResponse response = httpClient.GET(API_V1_HEALTH_NODE + nodeName);
            if (response.getStatus() == HttpStatus.OK_200) {
                return responseParser.parse(response.getContentAsString());
            }
        } catch (Exception e) {
            logger.error("failed to get node health: {}, cause={}", nodeName, e.getMessage());
        }
        return null;
    }

    public boolean putKeyValue(String key, String value) {
        try {
            ContentResponse response = httpClient.newRequest(API_V1_KV + key)
                    .method(HttpMethod.PUT)
                    .content(new StringContentProvider(value))
                    .send();
            logger.info("put key response: {}", response.getContentAsString());
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
                String encodedValue = kvMap.get("Value");
                if (StringUtils.isNotEmpty(encodedValue)) {
                    return new String(Base64.getDecoder().decode(encodedValue.getBytes()));
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            logger.error("failed to get kv: {}, cause: {}", key, e.getMessage());
        }
        return null;
    }
}