package com.github.lkq.smesh.consul.register;

import com.github.lkq.smesh.consul.client.ConsulClient;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@WebSocket
public class RegistrationWebSocket {
    private static Logger logger = LoggerFactory.getLogger(RegistrationWebSocket.class);

    private ConsulClient client;
    private Provider<ConsulRegistrar> registrarFactory;

    private Map<Session, ConsulRegistrar> registrars;

    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();

    public RegistrationWebSocket(ConsulClient client, Provider<ConsulRegistrar> registrarFactory) {
        this.client = client;
        this.registrarFactory = registrarFactory;
        this.registrars = new ConcurrentHashMap<>();
    }

    @OnWebSocketConnect
    public void connected(Session session) {
        logger.info("session connected: {}", session.getRemoteAddress());
        registrars.put(session, registrarFactory.get());
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        ConsulRegistrar register = registrars.get(session);
        if (register != null) {
            register.deRegister();
            registrars.remove(session);
        } else {
            logger.warn("register not found for session: {}", session.getRemoteAddress());
        }
        logger.info("session closed: {}", session.getRemoteAddress());
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        logger.info("service registration begin, session: {}, request: {}", session.getRemoteAddress(), message);
        ConsulRegistrar register = registrars.get(session);
        if (register != null) {
            String result = register.register(message);
            session.getRemote().sendString(result);
        } else {
            logger.error("session is invalid: {}", session.getRemoteAddress());
            session.getRemote().sendString("");
        }
    }
}
