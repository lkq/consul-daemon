package com.github.lkq.smesh.smesh4j;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static spark.Spark.*;

class RegisterClientTest {

    private static int port;
    private RegisterClient client;

    @BeforeAll
    static void startWebSocketServer() {
        port(0);
        webSocket("/test-ws", RegistrationWebSocket.class);
        init();
        awaitInitialization();
        port = port();
    }

    @Test
    void canConnectToServer() throws URISyntaxException, IOException {
        client = new RegisterClient(new URI("ws://localhost:" + port + "/test-ws"));
        delay();
        client.sayHello();
        delay();
    }

    private void delay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @WebSocket
    public static class RegistrationWebSocket {

        // Store sessions if you want to, for example, broadcast a message to all users
        private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();

        @OnWebSocketConnect
        public void connected(Session session) {
            sessions.add(session);
        }

        @OnWebSocketClose
        public void closed(Session session, int statusCode, String reason) {
            sessions.remove(session);
        }

        @OnWebSocketMessage
        public void message(Session session, String message) throws IOException {
            System.out.println("server got: " + message);   // Print message
            session.getRemote().sendString(message); // and send it back
        }
    }
}