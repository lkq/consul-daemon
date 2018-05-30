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
    private ResponseFactory responseFactory;

    private Map<Session, ConsulRegistrar> registrars;

    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();

    public RegistrationWebSocket(ConsulClient client, Provider<ConsulRegistrar> registrarFactory, ResponseFactory responseFactory) {
        this.client = client;
        this.registrarFactory = registrarFactory;
        this.responseFactory = responseFactory;
        this.registrars = new ConcurrentHashMap<>();
    }

    @OnWebSocketConnect
    public void connected(Session session) {
        logger.info("session connected: {}", session.getRemoteAddress());
        if (!registrars.containsKey(session)) {
            registrars.put(session, registrarFactory.get());
        } else {
            logger.error("session already exists: {}", session.getRemoteAddress());
        }
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        ConsulRegistrar register = registrars.get(session);
        if (register != null) {
            register.deRegister();
            registrars.remove(session);
            logger.info("session closed: {}", session.getRemoteAddress());
        } else {
            logger.error("register not found for session: {}", session.getRemoteAddress());
        }
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        logger.info("service registration begin, session: {}, request: {}", session.getRemoteAddress(), message);
        ConsulRegistrar registrar = registrars.get(session);
        if (registrar != null) {
            String result = registrar.register(message);
            String response = responseFactory.responseSuccess(result);
            session.getRemote().sendString(response);
        } else {
            logger.error("session is invalid: {}", session.getRemoteAddress());
            String response = responseFactory.responseFail("not connected", String.valueOf(session.getRemoteAddress()));
            session.getRemote().sendString(response);
        }
    }
}
