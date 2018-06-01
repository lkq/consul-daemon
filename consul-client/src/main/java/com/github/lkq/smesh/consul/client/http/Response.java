package com.github.lkq.smesh.consul.client.http;

public class Response {
    private final int status;
    private final String body;

    public Response(int status, String body) {
        this.status = status;
        this.body = body;
    }

    public int status() {
        return status;
    }

    public String body() {
        return body;
    }

    @Override
    public String toString() {
        return "Response{" +
                "status=" + status +
                ", body='" + body + '\'' +
                '}';
    }
}
