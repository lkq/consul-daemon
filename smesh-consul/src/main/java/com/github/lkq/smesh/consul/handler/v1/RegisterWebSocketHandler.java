package com.github.lkq.smesh.consul.handler.v1;

import com.github.lkq.smesh.consul.client.ServiceRegistrar;
import com.github.lkq.smesh.consul.client.http.Response;
import com.github.lkq.smesh.consul.register.ResponseFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@WebSocket
public class RegisterWebSocketHandler {
    private static Logger logger = LoggerFactory.getLogger(RegisterWebSocketHandler.class);

    private ResponseFactory responseFactory;

    private Map<Session, String> services;
    private ServiceRegistrar registrar;

    @Inject
    public RegisterWebSocketHandler(ServiceRegistrar registrar, ResponseFactory responseFactory) {
        this.registrar = registrar;
        this.responseFactory = responseFactory;
        this.services = new ConcurrentHashMap<>();
    }

    @OnWebSocketConnect
    public void connected(Session session) {
        logger.info("session connected: {}", session.getRemoteAddress());
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        try {
            String service = services.get(session);
            if (StringUtils.isNotBlank(service)) {
                logger.info("de-registering service, session: {}", session.getRemoteAddress());
                Response response = registrar.deRegister(service);
                logger.info("de-register result: {}", response);
                services.remove(session);
            } else {
                logger.error("session doesn't binded to any service registration: {}", session.getRemoteAddress());
            }
        } catch (Exception e) {
            logger.error("failed when closing connection");
        }
    }

    @OnWebSocketMessage
    public void message(Session session, String service) {
        logger.info("registering service, session: {}, request: {}", session.getRemoteAddress(), service);
        try {
            services.putIfAbsent(session, service);

            String response;
            try {
                Response result = registrar.register(service);
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
