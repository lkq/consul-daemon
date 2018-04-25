package com.lkq.services.docker.daemon.exception;

public class ConsulDaemonException extends RuntimeException {
    public ConsulDaemonException(String message) {
        super(message);
    }
    public ConsulDaemonException(String message, Throwable cause) {
        super(message, cause);
    }
}
