package com.github.lkq.smesh.consul.env.aws;

import java.util.List;

public interface EC2 {
    boolean isEc2();

    String getPrivateIP();
    
    String getTagValue(String key, String defaultValue);

    String getTagValue(String key);

    List<String> getInstanceIPByTag(String key);

    List<String> getInstanceIPByTagValue(String key, String value);
}
