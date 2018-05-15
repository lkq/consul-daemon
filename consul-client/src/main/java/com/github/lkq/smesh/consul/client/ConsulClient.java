package com.github.lkq.smesh.consul.client;

import com.github.lkq.smesh.consul.client.http.Response;
import com.github.lkq.smesh.consul.client.http.SimpleHttpClient;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.Map;

public class ConsulClient {
    private static Logger logger = LoggerFactory.getLogger(ConsulClient.class);

    private static final String API_HOST = "http://localhost";

    private String API_V1;
    private String API_V1_KV;
    private String API_V1_HEALTH_NODE;

    private final SimpleHttpClient httpClient;
    private final ResponseParser responseParser;

    public ConsulClient(SimpleHttpClient httpClient, ResponseParser responseParser, int port) {
        this.httpClient = httpClient;
        this.responseParser = responseParser;
        this.API_V1 = API_HOST + ":" + port + "/v1/";
        this.API_V1_KV = API_V1 + "kv/";
        this.API_V1_HEALTH_NODE = API_V1 + "health/node/";
    }

    public Map<String, String> getNodeHealth(String nodeName) {
        try {
            Response response = httpClient.get(API_V1_HEALTH_NODE + nodeName);
            if (response.status() == HttpStatus.OK_200) {
                return responseParser.parse(response.body());
            }
        } catch (Exception e) {
            logger.error("failed to get node health: {}, cause={}", nodeName, e.getMessage());
        }
        return null;
    }

    public boolean putKeyValue(String key, String value) {
        try {
            String uri = API_V1_KV + key;
            Response response = httpClient.put(uri, value);
            logger.debug("put kv result: [{}], url: {}", response.body(), uri);
            if (response.status() == 200) {
                return Boolean.valueOf(response.body());
            }
        } catch (Exception e) {
            logger.error("failed to put kv " + key + "=" + value, e);
        }
        return false;
    }

    public String getKeyValue(String key) {
        String value = "";
        try {
            String uri = API_V1_KV + key;
            Response response = httpClient.get(uri);
            if (response.status() == 200) {
                Map<String, String> kvMap = responseParser.parse(response.body());
                value = kvMap.get("Value");
                if (value != null && !"".equals(value.trim())) {
                    value = new String(Base64.getDecoder().decode(value.getBytes()));
                }
            }
            logger.debug("get kv {}={}, url: {}", key, value, uri);
            return value;
        } catch (Exception e) {
            logger.error("failed to get kv " + key, e.getMessage());
        }
        return value;
    }

    public static class Builder {

        private SimpleHttpClient httpClient;
        private ResponseParser responseParser;
        private int port = 8500;

        public Builder httpClient(SimpleHttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public Builder responseParser(ResponseParser responseParser) {
            this.responseParser = responseParser;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public ConsulClient build() {
            return new ConsulClient(httpClient == null ? new SimpleHttpClient() : httpClient,
                    responseParser == null ? new ResponseParser() : responseParser,
                    port);
        }
    }

}










