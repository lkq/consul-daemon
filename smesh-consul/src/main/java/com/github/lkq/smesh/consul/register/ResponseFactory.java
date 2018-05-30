package com.github.lkq.smesh.consul.register;

import com.google.gson.JsonObject;

public class ResponseFactory {
    public String responseSuccess(String message) {
        JsonObject res = new JsonObject();
        res.addProperty("status", "success");
        res.addProperty("message", message);
        return res.toString();
    }

    public String responseFail(String reason, String message) {
        JsonObject res = new JsonObject();
        res.addProperty("status", "fail");
        res.addProperty("reason", reason);
        res.addProperty("message", message);
        return res.toString();
    }

}
