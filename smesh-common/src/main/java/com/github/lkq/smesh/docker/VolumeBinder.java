package com.github.lkq.smesh.docker;

public class VolumeBinder {
    private String hostPath;
    private String containerPath;

    public VolumeBinder(String hostPath, String containerPath) {
        this.hostPath = hostPath;
        this.containerPath = containerPath;
    }

    public String hostPath() {
        return hostPath;
    }

    public String containerPath() {
        return containerPath;
    }

    @Override
    public String toString() {
        return "VolumeBinder{" +
                "hostPath='" + hostPath + '\'' +
                ", containerPath='" + containerPath + '\'' +
                '}';
    }
}
