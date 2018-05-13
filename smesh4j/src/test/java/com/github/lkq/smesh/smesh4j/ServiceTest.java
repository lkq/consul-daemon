package com.github.lkq.smesh.smesh4j;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.skyscreamer.jsonassert.JSONAssert;

class ServiceTest {
    private static Logger logger = LoggerFactory.getLogger(ServiceTest.class);

    @Test
    void canBuildServiceDefinition() throws JSONException {
        String service = new Service()
                .withName("test-service")
                .withAddress("127.0.0.1")
                .withPort("1234")
                .withTags("tag1", "tag2", "tag3")
                .withID("id123")
                .withHttpCheck("http://localhost:1234", "5s")
                .build();

        logger.info(() -> "service definition: " + service);
        JSONAssert.assertEquals(service, "{\n" +
                "  \"address\": \"127.0.0.1\",\n" +
                "  \"port\": \"1234\",\n" +
                "  \"name\": \"test-service\",\n" +
                "  \"id\": \"id123\",\n" +
                "  \"check\": {\n" +
                "    \"http\": \"http://localhost:1234\",\n" +
                "    \"interval\": \"5s\"\n" +
                "  },\n" +
                "  \"tags\": [\n" +
                "    \"tag1\",\n" +
                "    \"tag2\",\n" +
                "    \"tag3\"\n" +
                "  ]\n" +
                "}", true);
    }
}