package com.github.lkq.smesh.consul.client;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.Map;

public class ConsulClient {
    private static Logger logger = LoggerFactory.getLogger(ConsulClient.class);

    private static final String API_HOST = "http://localhost:8500";

    private String API_V1;
    private String API_V1_KV;
    private String API_V1_HEALTH_NODE;

    private final HttpClient httpClient;
    private final ResponseParser responseParser;

    public ConsulClient(HttpClient httpClient, ResponseParser responseParser, int port) {
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
            String uri = API_V1_KV + key;
            ContentResponse response = httpClient.newRequest(uri)
                    .method(HttpMethod.PUT)
                    .content(new StringContentProvider(value))
                    .send();
            logger.debug("put kv result: [{}], url: {}", response.getContentAsString(), uri);
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
            String value = "";
            String uri = API_V1_KV + key;
            ContentResponse response = httpClient.GET(uri);
            if (response.getStatus() == 200) {
                Map<String, String> kvMap = responseParser.parse(response.getContentAsString());
                String encodedValue = kvMap.get("Value");
                if (encodedValue != null && !"".equals(encodedValue.trim())) {
                    value = new String(Base64.getDecoder().decode(encodedValue.getBytes()));
                }
            }
            logger.debug("get kv result: [{}], url: {}", value, uri);
            return value;
        } catch (Exception e) {
            logger.error("failed to get kv: {}, cause: {}", key, e.getMessage());
        }
        return null;
    }


}
