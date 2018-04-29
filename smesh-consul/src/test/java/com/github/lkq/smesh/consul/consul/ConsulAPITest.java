package com.github.lkq.smesh.consul.consul;

import com.github.lkq.smesh.consul.api.ConsulAPI;
import com.github.lkq.smesh.consul.api.ConsulResponseParser;
import com.github.lkq.smesh.docker.PortBinder;
import com.github.lkq.smesh.exception.SmeshException;
import com.github.lkq.smesh.consul.IntegrationTest;
import com.github.lkq.smesh.consul.LocalLauncher;
import com.github.lkq.smesh.consul.MacEnvironment;
import org.eclipse.jetty.client.HttpClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsulAPITest {
    public static final String NODE_NAME = "consul-api-test";
    private static int apiPort;
    private ConsulAPI consulAPI;
    private ConsulResponseParser responseParser = new ConsulResponseParser();

    @BeforeAll
    static void startTestServer() {

        apiPort = randomPort();
        MacEnvironment env = new MacEnvironment() {
            @Override
            public String nodeName() {
                return NODE_NAME;
            }

            @Override
            public Boolean forceRestart() {
                return true;
            }

            @Override
            public int consulAPIPort() {
                return apiPort;
            }
        };
        new LocalLauncher().launch(env, Arrays.asList(new PortBinder(apiPort, 8500, PortBinder.Protocol.TCP)));
    }

    private static int randomPort() {
        try (ServerSocket s = new ServerSocket(0)) {
            return s.getLocalPort();
        } catch (IOException e) {
            throw new SmeshException("no available port", e);
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        HttpClient client = new HttpClient();
        client.start();
        consulAPI = new ConsulAPI(client, responseParser, apiPort);
    }

    @IntegrationTest
    @Test
    void canPutKeyValue() {
        assertTrue(consulAPI.putKeyValue("test-key", "test-value"));
    }

    @IntegrationTest
    @Test
    void canGetKeyValue() {
        consulAPI.putKeyValue("test-key", "test-value1");
        String value = consulAPI.getKeyValue("test-key");
        assertThat(value, is("test-value1"));
    }

    @IntegrationTest
    @Test
    void canGetNodeHealth() {
        Map<String, String> nodeHealth = consulAPI.getNodeHealth(NODE_NAME);
        assertThat(nodeHealth.get("Status"), is("passing"));
    }
}