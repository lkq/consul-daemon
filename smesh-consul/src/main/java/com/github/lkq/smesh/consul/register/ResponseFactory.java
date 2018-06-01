package com.github.lkq.smesh.consul.register;

import com.github.lkq.smesh.consul.client.http.Response;
import com.google.gson.JsonObject;

public class ResponseFactory {
    public String responseNormal(Response response) {
        JsonObject res = new JsonObject();
        res.addProperty("status", response.status() == 200 ? "success" : "fail");
        res.addProperty("code", response.status());
        res.addProperty("message", response.body());
        return res.toString();
    }

    public String responseError(String reason, String message) {
        JsonObject res = new JsonObject();
        res.addProperty("status", "error");
        res.addProperty("reason", reason);
        res.addProperty("message", message);
        return res.toString();
    }

}
