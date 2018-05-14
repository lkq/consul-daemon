package com.github.lkq.smesh.consul.client;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ResponseParser {

    private static Logger logger = LoggerFactory.getLogger(ResponseParser.class);
    public Map<String, String> parse(String content) {

        List contentList = new Gson().fromJson(content, ArrayList.class);
        if (contentList.size() > 1) {
            logger.warn("multiple entries in response, will keep the first one: {}", contentList);
        }
        if (contentList.size() > 0) {
            return (Map<String, String>) contentList.get(0);
        }
        return Collections.emptyMap();
    }
}
