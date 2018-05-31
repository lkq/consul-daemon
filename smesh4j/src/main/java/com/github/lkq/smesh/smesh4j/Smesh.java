package com.github.lkq.smesh.smesh4j;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Smesh implements ReconnectListener {

    private static Logger logger = Logger.getLogger(Smesh.class.getName());

    private static final String BASE_URL = "http://localhost:8500";
    private static final String DEREG_URL = BASE_URL + "/v1/agent/service/deregister/";
    private static final String REG_URL = BASE_URL + "/v1/agent/service/register";
    private static final String KV_URL = BASE_URL + "/v1/kv/";
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    private String[] uris;
    private String service;
    private WebSocketClientFactory webSocketFactory;
    private int retryURI = 0;

    private boolean alive = true;

    private RegisterClient client;
    private WebSocketClient webSocketClient;

    private Smesh(String[] uris, String service, WebSocketClientFactory webSocketFactory) {
        this.uris = uris;
        this.service = service;
        this.webSocketFactory = webSocketFactory;
    }

    public static Smesh register(String[] registerURI, String service, WebSocketClientFactory webSocketFactory, int reconnectIntervalInMS) {
        Smesh smesh = new Smesh(registerURI, service, webSocketFactory);
        scheduler.scheduleAtFixedRate(smesh::tryNextURI, 0, reconnectIntervalInMS, TimeUnit.MILLISECONDS);
        return smesh;
    }

    public void deRegister() {
        this.alive = false;
    }

    private synchronized void tryNextURI() {
        try {
            if (alive) {
                if (webSocketClient == null) {
                    String uri = this.uris[retryURI++];
                    if (retryURI >= uris.length) {
                        retryURI = 0;
                    }
                    webSocketClient = webSocketFactory.create(URI.create(uri), service, this);
                    webSocketClient.start();
                } else {
                    logger.info("service already registered");
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "failed to register service", e);
        }
    }

    @Override
    public void onDisconnect() {
        webSocketClient.stop();
        webSocketClient = null;
    }

    public Smesh(URI uri) {
        client = new RegisterClient(uri);
    }
    public void register(String service) {
        client.register(service);
        logger.info("registered service: " + service);
    }
}
