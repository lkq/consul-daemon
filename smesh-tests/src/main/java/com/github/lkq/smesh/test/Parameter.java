package com.github.lkq.smesh.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public class Parameter {
    private static Logger logger = LoggerFactory.getLogger(Parameter.class);
    private URI consulURI;

    public static Parameter load(String[] args) {
        try {
            Parameter param = new Parameter();
            if (args.length > 0) {
                param.consulURI = new URI(args[0]);
            } else {
                param.consulURI = new URI("ws://localhost:1025/register");
            }
            return param;
        } catch (URISyntaxException e) {
            logger.error("failed to parse registration uri: " + args[0] + ", sample: ws://localhost:8080/register");
            throw new RuntimeException("failed to parse registration uri", e);
        }
    }

    public URI consulURI() {
        return consulURI;
    }
}
