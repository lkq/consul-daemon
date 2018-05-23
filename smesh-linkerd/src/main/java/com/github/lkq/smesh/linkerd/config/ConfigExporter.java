package com.github.lkq.smesh.linkerd.config;

import com.github.lkq.smesh.Constants;
import com.github.lkq.smesh.exception.SmeshException;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * write linkerd config to a folder, which later will be bind to a volume of the container
 */
public class ConfigExporter {

    private Yaml yaml = new Yaml();

    public String writeToFile(File dest, Map config) {
        try {
            if (!dest.getParentFile().exists() && !dest.getParentFile().mkdirs()) {
                throw new SmeshException("failed to create parent folders: " + dest.getParent());
            }
            FileUtils.write(dest, yaml.dumpAsMap(config), Constants.ENCODING_UTF8);
            return dest.getAbsolutePath();
        } catch (IOException e) {
            throw new SmeshException("failed to export linkerd config to file: " + dest.getAbsolutePath(), e);
        }
    }

    public Map loadFromResource(String resourcePath) {
        try {
            InputStream configStream = ClassLoader.getSystemResourceAsStream(resourcePath);
            return yaml.loadAs(configStream, HashMap.class);
        } catch (Exception e) {
            throw new SmeshException("failed to load config from: " + resourcePath, e);
        }
    }
}
