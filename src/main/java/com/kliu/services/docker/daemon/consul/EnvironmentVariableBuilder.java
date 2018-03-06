package com.kliu.services.docker.daemon.consul;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentVariableBuilder {

    private static Logger logger = LoggerFactory.getLogger(EnvironmentVariableBuilder.class);

    public Map<String, Object> build() {

        Map<String, Object> env = new HashMap<>();
        Map<String, Object> consulLocalConfig = new HashMap<>();
        consulLocalConfig.put("skip_leave_on_interrupt", true);
        env.put("CONSUL_LOCAL_CONFIG", consulLocalConfig);
//        env.put("CONSUL_BIND_INTERFACE", "en1");

        logger.info("consul docker env variables: {}", new Gson().toJson(env));
        return env;
    }
}
