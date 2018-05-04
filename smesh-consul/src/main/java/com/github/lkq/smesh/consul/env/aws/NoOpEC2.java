package com.github.lkq.smesh.consul.env.aws;

import java.util.Collections;
import java.util.List;

public class NoOpEC2 implements EC2 {

    @Override
    public boolean isEc2() {
        return false;
    }

    @Override
    public String getPrivateIP() {
        return "";
    }

    @Override
    public String getTagValue(String key, String defaultValue) {
        return "";
    }

    @Override
    public String getTagValue(String key) {
        return "";
    }

    @Override
    public List<String> getInstanceIPByTag(String key) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getInstanceIPByTagValue(String key, String value) {
        return Collections.emptyList();
    }
}




