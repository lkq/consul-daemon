package com.github.lkq.smesh.consul.client.http;

import okhttp3.Response;

public class HttpException extends RuntimeException {
    private Response response;

    public HttpException(Response response) {
        this.response = response;
    }

    public HttpException(Exception cause) {
        super(cause);
    }

    public Response response() {
        return response;
    }
}
