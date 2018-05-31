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
        try {
            logger.info("session connected: {}", session.getRemoteAddress());
            if (!registrars.containsKey(session)) {
                registrars.put(session, registrarFactory.get());
            } else {
                logger.error("session already exists: {}", session.getRemoteAddress());
            }
        } catch (Exception e) {
            logger.error("failed when session connected", e);
        }
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        try {
            ConsulRegistrar register = registrars.get(session);
            if (register != null) {
                logger.info("de-registering service, session: {}", session.getRemoteAddress());
                register.deRegister();
                registrars.remove(session);
            } else {
                logger.error("register not found for session: {}", session.getRemoteAddress());
            }
        } catch (Exception e) {
            logger.error("failed when closing connection");
        }
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        logger.info("registering service, session: {}, request: {}", session.getRemoteAddress(), message);
        try {
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
        } catch (Exception e) {
            logger.error("failed to register service", e);
        }
    }
}
