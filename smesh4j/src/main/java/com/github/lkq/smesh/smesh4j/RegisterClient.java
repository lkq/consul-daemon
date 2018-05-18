package com.github.lkq.smesh.smesh4j;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@WebSocket
public class RegisterClient {
    private static Logger logger = Logger.getLogger(RegisterClient.class.getName());
    private Session session;
    private CountDownLatch connected = new CountDownLatch(1);

    public RegisterClient(URI uri) {
        try {
            logger.info("connecting to " + uri);
            WebSocketClient client = new WebSocketClient();
            client.start();
            client.connect(this, uri);
            connected.await(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new SmeshException("failed to connect to server " + uri, e);
        }
    }

    @OnWebSocketConnect
    public void onOpen(Session session) {
        this.session = session;
        logger.info("session open:" + this.session);
        connected.countDown();
    }

    @OnWebSocketClose
    public void onClose(Session session, int status, String reason) {
        logger.info("session closed:" + session + ", reason:" + reason);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable reason) {
        logger.info("session error:" + session + ", reason:" + reason);
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        logger.info("client got: " + message);
    }

    public void register(String service) {
        try {
            session.getRemote().sendString(service);
        } catch (IOException e) {
            logger.severe("failed to register service: " + service);
        }
    }
}
