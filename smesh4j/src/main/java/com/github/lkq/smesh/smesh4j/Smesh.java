package com.github.lkq.smesh.smesh4j;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

import java.util.logging.Logger;

public class Smesh {

    private static Logger logger = Logger.getLogger(Smesh.class.getName());

    private static final String BASE_URL = "http://localhost:8500";
    private static final String DEREG_URL = BASE_URL + "/v1/agent/service/deregister/";
    private static final String REG_URL = BASE_URL + "/v1/agent/service/register";
    private static final String KV_URL = BASE_URL + "/v1/kv/";
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    public void register(String service) {
        logger.info("registered service: " + service);
    }
}
