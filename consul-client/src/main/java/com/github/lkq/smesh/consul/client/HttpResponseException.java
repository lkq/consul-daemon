package com.github.lkq.smesh.consul.client;

import okhttp3.Response;

public class HttpResponseException extends RuntimeException {
    private Response response;

    public HttpResponseException(Response response) {
        this.response = response;
    }

    public HttpResponseException(Exception cause) {
        super(cause);
    }

    public Response response() {
        return response;
    }
}
