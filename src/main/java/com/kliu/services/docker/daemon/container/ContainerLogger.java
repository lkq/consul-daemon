package com.kliu.services.docker.daemon.container;

import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import org.slf4j.LoggerFactory;

public class ContainerLogger extends LogContainerResultCallback {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(ContainerLogger.class);

    @Override
    public void onNext(Frame frame) {
        logger.info(new String(frame.getPayload()));
    }
}
