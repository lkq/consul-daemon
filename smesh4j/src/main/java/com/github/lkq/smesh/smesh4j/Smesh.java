package com.github.lkq.smesh.smesh4j;

import java.net.URI;
import java.util.logging.Logger;

public class Smesh {

    private static Logger logger = Logger.getLogger(Smesh.class.getName());

    private static final String BASE_URL = "http://localhost:8500";
    private static final String DEREG_URL = BASE_URL + "/v1/agent/service/deregister/";
    private static final String REG_URL = BASE_URL + "/v1/agent/service/register";
    private static final String KV_URL = BASE_URL + "/v1/kv/";

    private RegisterClient client;

    public Smesh(URI uri) {
        client = new RegisterClient(uri);
    }
    public void register(String service) {
        client.register(service);
        logger.info("registered service: " + service);
    }
}
