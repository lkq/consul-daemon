package com.github.lkq.smesh.consul.profile;

import java.util.HashMap;
import java.util.Map;

public class Profile {
    private String name;
    private String version;
    private long millis;
    private String checksum;

    private Map<String, Object> details = new HashMap<>();
    private String nodeName;

    public String name() {
        return name;
    }

    public Profile name(String name) {
        this.name = name;
        return this;
    }

    public String version() {
        return version;
    }

    public Profile version(String version) {
        this.version = version;
        return this;
    }

    public String nodeName() {
        return nodeName;
    }

    public Profile nodeName(String nodeName) {
        this.nodeName = nodeName;
        return this;
    }

    public long millis() {
        return millis;
    }

    public Profile millis(long millis) {
        this.millis = millis;
        return this;
    }

    public String checksum() {
        return checksum;
    }

    public Profile checksum(String checksum) {
        this.checksum = checksum;
        return this;
    }

    public Map<String, Object> details() {
        return details;
    }

    public Profile details(Map<String, Object> details) {
        this.details = details;
        return this;
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\":\"" + name + "\"" +
                ", \"version\":\"" + version + "\"" +
                ", \"millis\":" + millis +
                ", \"checksum\":\"" + checksum + "\"" +
                ", \"details\":" + details +
                '}';
    }
}
