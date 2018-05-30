package com.github.lkq.smesh.consul.client;

import com.github.lkq.smesh.consul.client.http.Response;
import com.github.lkq.smesh.consul.client.http.SimpleHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ConsulClient {
    private static Logger logger = LoggerFactory.getLogger(ConsulClient.class);

    private String API_V1;
    private String API_V1_KV;
    private String API_V1_REG;
    private String API_V1_HEALTH_NODE;

    private final SimpleHttpClient httpClient;
    private final ResponseParser responseParser;

    public ConsulClient(SimpleHttpClient httpClient, ResponseParser responseParser, String baseURL) {
        this.httpClient = httpClient;
        this.responseParser = responseParser;
        this.API_V1 = baseURL + "/v1/";
        this.API_V1_KV = API_V1 + "kv/";
        this.API_V1_REG = API_V1 + "agent/service/register";
        this.API_V1_HEALTH_NODE = API_V1 + "health/node/";
    }

    public Map<String, String> getNodeHealth(String nodeName) {
        try {
            Response response = httpClient.get(API_V1_HEALTH_NODE + nodeName);
            if (response.status() == 200) {
                return responseParser.parse(response.body());
            } else {
                logger.error("consul is not healthy: " + response);
            }
        } catch (Exception e) {
            logger.error("failed to get node handler: {}, cause={}", nodeName, e.getMessage());
        }
        Map<String, String> error = new HashMap<>();
        error.put("Status", "failing");
        return error;
    }

    public boolean putKeyValue(String key, String value) {
        String uri = API_V1_KV + key;
        Response response = httpClient.put(uri, value);
        logger.debug("put kv: [{}], url: {}", response.body(), uri);
        if (response.status() == 200) {
            return Boolean.valueOf(response.body());
        }
        return false;
    }

    public String getKeyValue(String key) {
        String value = "";
        String uri = API_V1_KV + key;
        Response response = httpClient.get(uri);
        if (response.status() == 200) {
            Map<String, String> kvMap = responseParser.parse(response.body());
            value = kvMap.get("Value");
            if (value != null && !"".equals(value.trim())) {
                value = new String(Base64.getDecoder().decode(value.getBytes()));
            }
        }
        logger.debug("get kv: {}={}, url: {}", key, value, uri);
        return value;
    }

    public Response register(String service) {
        return httpClient.put(API_V1_REG, service);
    }

    public static class Builder {

        private SimpleHttpClient httpClient;
        private ResponseParser responseParser;
        private String baseURL = "http://localhost";
        private int port = 8500;

        public Builder httpClient(SimpleHttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public Builder responseParser(ResponseParser responseParser) {
            this.responseParser = responseParser;
            return this;
        }

        public Builder baseURL(String baseURL) {
            this.baseURL = baseURL;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public ConsulClient build() {
            return new ConsulClient(httpClient == null ? new SimpleHttpClient() : httpClient,
                    responseParser == null ? new ResponseParser() : responseParser,
                    baseURL);
        }
    }

}










