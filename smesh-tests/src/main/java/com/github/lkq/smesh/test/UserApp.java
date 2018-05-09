package com.github.lkq.smesh.test;

import static spark.Spark.get;
import static spark.Spark.port;

public class UserApp {
    public static void main(String[] args) {
        UserService userService = new UserService();
        port(8080);
        get("/users/:name", (req, res) -> userService.getUser(req.params("name")));
    }
}
