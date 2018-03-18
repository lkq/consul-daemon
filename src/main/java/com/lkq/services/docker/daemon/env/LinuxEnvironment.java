package com.lkq.services.docker.daemon.env;

import com.lkq.services.docker.daemon.exception.ConsulDaemonException;
import spark.utils.StringUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

public class LinuxEnvironment implements Environment {

    private String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        if (StringUtils.isEmpty(value)) {
            value = System.getProperty(key, defaultValue);
        }
        return value;
    }

    @Override
    public String nodeName() {
        String nodeName = getEnv(Environment.ENV_NODE_NAME, "");
        if (StringUtils.isEmpty(nodeName)) {
            throw new ConsulDaemonException("please provide node name by export consul.nodeName=<name> or -Dconsul.nodeName=<name>");
        }
        return nodeName;
    }

    @Override
    public Environment.ConsulRole consulRole() {
        String tagValue = getEnv(ENV_CONSUL_ROLE, "");
        if ("server".equals(tagValue)) {
            return Environment.ConsulRole.SERVER;
        }
        return Environment.ConsulRole.CLIENT;
    }

    @Override
    public List<String> clusterMembers() {
        String members = getEnv(ENV_CONSUL_CLUSTER_MEMBER, "");
        ArrayList<String> clusterMembers = new ArrayList<>();
        String[] split = members.split(" ");
        for (String host : split) {
            if (StringUtils.isNotEmpty(host)) {
                clusterMembers.add(host);
            }
        }
        return clusterMembers;
    }

    @Override
    public String dataPath() {
        return Paths.get(".").toAbsolutePath().normalize().toString() + "/data";
    }

    @Override
    public String network() {
        return "host";
    }

    @Override
    public Boolean forceRestart() {
        String forceRestart = System.getProperty("forceRestart");
        if (StringUtils.isEmpty(forceRestart)) {
            return null;
        }
        return "true".equalsIgnoreCase(forceRestart);
    }

    @Override
    public String appVersion() {
        String jarPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            JarFile jarFile = new JarFile(jarPath);
            return (String) jarFile.getManifest().getMainAttributes().get("Bundle-Version");
        } catch (IOException e) {
            throw new ConsulDaemonException("failed to get jar version", e);
        }
    }

    @Override
    public int servicePort() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        } catch (IOException e) {
            throw new ConsulDaemonException("failed to get available port", e);
        }
    }

    @Override
    public int consulAPIPort() {
        return 8500;
    }
}
