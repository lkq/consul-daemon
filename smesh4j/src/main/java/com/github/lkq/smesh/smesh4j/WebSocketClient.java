package com.github.lkq.smesh.smesh4j;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebSocket
public class WebSocketClient {
    private static Logger logger = Logger.getLogger(WebSocketClient.class.getName());

    private final URI uri;
    private final String service;
    private final ReconnectListener reconnectListener;

    private Session session;
    private CountDownLatch connected = new CountDownLatch(1);
    private org.eclipse.jetty.websocket.client.WebSocketClient client;

    public WebSocketClient(URI uri, String service, ReconnectListener reconnectListener) {
        this.uri = uri;
        this.service = service;
        this.reconnectListener = reconnectListener;
    }

    public void start() {
        try {
            logger.info("connecting to " + uri);
            client = new org.eclipse.jetty.websocket.client.WebSocketClient();
            client.start();
            client.connect(this, uri);
            connected.await(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new SmeshException("failed to connect to server " + uri, e);
        }
    }

    public void stop() {
        try {
            this.client.stop();
            this.client = null;
        } catch (Exception e) {
            logger.log(Level.WARNING, "failed to stop websocket client", e);
        }
    }

    @OnWebSocketConnect
    public void onOpen(Session session) {
        this.session = session;
        logger.info("connection established" + this.session);
        try {
            session.getRemote().sendString(service);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "failed to register service", e);
        }
        connected.countDown();
    }

    @OnWebSocketClose
    public void onClose(Session session, int status, String reason) {
        logger.info("connection closed: " + session + ", reason:" + reason);
        reconnectListener.onDisconnect();
    }

    @OnWebSocketError
    public void onError(Session session, Throwable reason) {
        logger.info("connection error: " + session + ", reason:" + reason);
        reconnectListener.onDisconnect();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        logger.info("service registration result: " + message);
    }

}
