package com.github.lkq.smesh.test;

import com.github.lkq.smesh.smesh4j.Service;
import com.github.lkq.smesh.smesh4j.Smesh;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static spark.Spark.get;
import static spark.Spark.port;

public class UserApp {
    public static void main(String[] args) {
        UserService userService = new UserService();
        port(8081);
        get("/users/:name", (req, res) -> userService.getUser(req.params("name")));

        try {
            String service = Service.create()
                    .withID("user-app-" + System.currentTimeMillis())
                    .withName("user-app")
                    .withAddress(InetAddress.getLocalHost().getHostAddress())
                    .withPort(8081).build();
            new Smesh().register(service);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
