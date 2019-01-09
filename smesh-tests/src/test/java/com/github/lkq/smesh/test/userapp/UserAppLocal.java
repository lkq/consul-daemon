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

import java.util.Arrays;

import static com.github.lkq.smesh.test.TestEngine.USER_APP;
import static org.slf4j.LoggerFactory.getLogger;

public class UserAppLocal {
    private static final Logger logger = getLogger(UserAppLocal.class);

    private UserAppPackager packager = new UserAppPackager();
    private UserAppImageBuilder imageBuilder = new UserAppImageBuilder(DockerClientFactory.create());
    private InstaDocker userApp;

    public static void main(String[] args) {
        new UserAppLocal().start(8081, 1025);
    }

    public void start(int port, int smeshPort) {
        String registerURL = "ws://localhost:" + smeshPort + "/smesh/register/v1";
        String[] artifact = packager.buildPackage();

        logger.info("installing test app: {}{}", artifact[0], artifact[1]);
        String image = imageBuilder.build(artifact[0], artifact[1], registerURL, USER_APP);
        userApp = new InstaDocker(image, USER_APP)
                .dockerLogger(getLogger("UserApp"))
                .init();
        DockerContainer container = userApp.container();
        container.portBindings(Arrays.asList(new com.github.lkq.instadocker.docker.entity.PortBinding(port)));

        if (container.ensureExists() && container.ensureRunning()) {
            String containerId = container.containerId().get();
            registerUserApp(containerId, port, registerURL);
        } else {
            throw new SmeshException("failed to start user app");
        }
    }

    public void stop() {
        userApp.container().ensureStopped(60);
    }

    private void registerUserApp(String containerId, int restPort, String registerURL) {
        try {
            InspectContainerResponse container = DockerClientFactory.create().inspectContainerCmd(containerId).exec();
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
