package com.github.lkq.smesh.exception;

public class SmeshException extends RuntimeException {
    public SmeshException(String message) {
        super(message);
    }
    public SmeshException(String message, Throwable cause) {
        super(message, cause);
    }
}
