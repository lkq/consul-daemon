package com.github.lkq.smesh.consul.register;

import com.github.lkq.smesh.consul.client.ConsulClient;
import com.github.lkq.smesh.consul.client.ServiceRegistrar;
import com.github.lkq.smesh.consul.client.http.Response;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class RegistrationWebSocket {
    private static Logger logger = LoggerFactory.getLogger(RegistrationWebSocket.class);

    private ConsulClient client;
    private ResponseFactory responseFactory;

    private Map<Session, ServiceRegistrar> registrars;

    public RegistrationWebSocket(ConsulClient client, ResponseFactory responseFactory) {
        this.client = client;
        this.responseFactory = responseFactory;
        this.registrars = new ConcurrentHashMap<>();
    }

    @OnWebSocketConnect
    public void connected(Session session) {
        logger.info("session connected: {}", session.getRemoteAddress());
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        try {
            ServiceRegistrar register = registrars.get(session);
            if (register != null) {
                logger.info("de-registering service, session: {}", session.getRemoteAddress());
                Response response = register.deRegister();
                logger.info("de-register result: {}", response);
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
            ServiceRegistrar registrar = registrars.computeIfAbsent(session, (s) -> new ServiceRegistrar(client, message));

            String response;
            try {
                Response result = registrar.register();
                response = responseFactory.responseNormal(result);
            } catch (Exception e) {
                response = responseFactory.responseError(e.getClass().getName(), e.getMessage());
            }
            session.getRemote().sendString(response);
        } catch (Exception e) {
            logger.error("failed to register service", e);
        }
    }
}
