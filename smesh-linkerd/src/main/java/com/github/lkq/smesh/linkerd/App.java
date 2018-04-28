package com.github.lkq.smesh.linkerd;

import com.github.lkq.smesh.context.ContainerContext;
import com.github.lkq.smesh.docker.DockerClientFactory;
import com.github.lkq.smesh.docker.SimpleDockerClient;
import com.github.lkq.smesh.linkerd.api.LinkerdController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static Logger logger = LoggerFactory.getLogger(App.class);

    /**
     * application entry point, a place to put together different pieces and make it run
     *
     */
    public App() {

    }

    /**
     * start the application
     *
     */
    public void start() {
        LinkerdController controller = new LinkerdController(SimpleDockerClient.create(DockerClientFactory.get()));
        ContainerContext context = new ContainerContext();
        controller.stopAndRemoveExistingInstance(context.nodeName());
        controller.startNewInstance(context);
        controller.attachLogging(context.nodeName());
        controller.attachLogging(context.nodeName());
    }

    public void stop() {

    }

}
