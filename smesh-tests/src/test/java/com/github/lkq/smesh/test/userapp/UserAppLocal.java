package com.github.lkq.smesh.test.userapp;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.lkq.instadocker.InstaDocker;
import com.github.lkq.instadocker.docker.DockerContainer;
import com.github.lkq.smesh.exception.SmeshException;
import com.github.lkq.smesh.smesh4j.Service;
import com.github.lkq.smesh.smesh4j.Smesh;
import com.github.lkq.smesh.smesh4j.WebSocketClientFactory;
import com.github.lkq.smesh.test.DockerClientFactory;
import com.github.lkq.smesh.test.app.UserAppImageBuilder;
import com.github.lkq.smesh.test.app.UserAppPackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static com.github.lkq.smesh.test.TestEngine.USER_APP;
import static org.slf4j.LoggerFactory.getLogger;

public class UserAppLocal {
    private static final Logger logger = getLogger(UserAppLocal.class);

    private UserAppPackager packager = new UserAppPackager();
    private UserAppImageBuilder imageBuilder = new UserAppImageBuilder(DockerClientFactory.get());
    private InstaDocker userApp;

    public static void main(String[] args) throws InterruptedException {
        final UserAppLocal userAppLocal = new UserAppLocal();
        userAppLocal.start(8081, "ws://localhost:1025/smesh/register/v1");
        Thread.sleep(3600000);
    }

    public void start(int port, String registrationURL) {
        String[] artifact = packager.buildPackage();

        logger.info("building user-app image: artifact={}, version={}", artifact[0], artifact[1]);
        String image = imageBuilder.build(artifact[0], artifact[1], registrationURL, USER_APP);
        userApp = new InstaDocker(image, USER_APP)
                .dockerLogger(LoggerFactory.getLogger("userapp"))
                .init();
        DockerContainer container = userApp.container();
        container.portBindings(Arrays.asList(new com.github.lkq.instadocker.docker.entity.PortBinding(port)));

        userApp.start(true, 60);
        if (container.exists()) {
            String containerId = container.containerId().get();
            registerUserApp(containerId, port, registrationURL);
        } else {
            throw new SmeshException("failed to start user app");
        }
    }

    public void stop() {
        userApp.container().ensureStopped(60);
    }


    /**
     * use local process to register user-app service to smesh-consul
     * due to smesh-consul is running as local web service, which can't be accessed from user-app container on mac
     * @param containerId
     * @param restPort
     * @param registerURL
     */
    private void registerUserApp(String containerId, int restPort, String registerURL) {
        try {
            InspectContainerResponse container = DockerClientFactory.get().inspectContainerCmd(containerId).exec();
            String address = container.getNetworkSettings().getNetworks().get("bridge").getIpAddress();
            String service = Service.create()
                    .withID("userapp-" + System.currentTimeMillis())
                    .withName("userapp")
                    .withAddress(address)
                    .withPort(restPort).build();

            Smesh.register(new String[]{registerURL}, service, new WebSocketClientFactory(), 10000);
        } catch (Exception e) {
            logger.error("failed to register user app", e);
        }
    }
}
