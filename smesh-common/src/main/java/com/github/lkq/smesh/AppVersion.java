package com.github.lkq.smesh;

import com.github.lkq.smesh.exception.SmeshException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

public class AppVersion {

    private static Logger logger = LoggerFactory.getLogger(AppVersion.class);

    public static String get(Class clz) {
        try {
            String jarPath = clz.getProtectionDomain().getCodeSource().getLocation().getPath();
            File path = new File(jarPath);
            if (path.exists() && path.isDirectory()) {
                logger.error("unable to get app version");
                return "unknown";
            }
            JarFile jarFile = new JarFile(jarPath);
            return (String) jarFile.getManifest().getMainAttributes().get("Bundle-Version");
        } catch (IOException e) {
            throw new SmeshException("failed to get jar version", e);
        }
    }
}
