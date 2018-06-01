package com.github.lkq.smesh.smesh4j;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Smesh implements ReconnectListener {

    private static Logger logger = Logger.getLogger(Smesh.class.getName());
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private String[] URIs;
    private String service;
    private WebSocketClientFactory webSocketFactory;
    private int currentURIIndex = 0;

    private boolean alive = true;

    private WebSocketClient webSocketClient;

    private Smesh(String[] URIs, String service, WebSocketClientFactory webSocketFactory) {
        this.URIs = URIs;
        this.service = service;
        this.webSocketFactory = webSocketFactory;
    }

    public static Smesh register(String[] registerURI, String service, WebSocketClientFactory webSocketFactory, int reconnectIntervalInMS) {
        Smesh smesh = new Smesh(registerURI, service, webSocketFactory);
        scheduler.scheduleAtFixedRate(smesh::tryNextURI, 0, reconnectIntervalInMS, TimeUnit.MILLISECONDS);
        logger.info("registering service: " + service);
        return smesh;
    }

    public void deRegister() {
        this.alive = false;
        webSocketClient.stop();
        webSocketClient = null;
    }

    private synchronized void tryNextURI() {
        try {
            if (alive) {
                if (webSocketClient == null) {
                    String uri = this.URIs[currentURIIndex++];
                    if (currentURIIndex >= URIs.length) {
                        currentURIIndex = 0;
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
}
