package com.github.lkq.smesh.linkerd.config;

import com.github.lkq.smesh.exception.SmeshException;
import org.apache.commons.io.FileUtils;
import org.ho.yaml.Yaml;

import java.io.File;
import java.io.IOException;

/**
 * write linkerd config to a folder, which later will be bind to a volume of the container
 */
public class ConfigExporter {
    public String export(LinkerdConfig config, File dest) {
        try {
            if (!dest.getParentFile().exists() && !dest.getParentFile().mkdirs()) {
                throw new SmeshException("failed to create parent folders: " + dest.getParent());
            }
            FileUtils.write(dest, Yaml.dump(config), "UTF-8");
            return dest.getAbsolutePath();
        } catch (IOException e) {
            throw new SmeshException("failed to export linkerd config file to " + dest.getAbsolutePath(), e);
        }
    }
}
