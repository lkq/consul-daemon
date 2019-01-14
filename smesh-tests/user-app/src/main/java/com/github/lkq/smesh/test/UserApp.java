package com.github.lkq.smesh.test;

import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;
import static spark.Spark.get;
import static spark.Spark.port;

public class UserApp {
    private static final Logger logger = getLogger(UserApp.class);

    public static void main(String[] args) {
        UserService userService = new UserService();
        port(8081);
        get("/users/:name", (req, res) -> userService.getUser(req.params("name")));
        logger.info("============== user-app started ==============");
    }
}
