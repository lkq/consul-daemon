package com.github.lkq.smesh.context;

public class VolumeBinding {
    private String hostPath;
    private String containerPath;

    public VolumeBinding(String hostPath, String containerPath) {
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
        return "VolumeBinding{" +
                "hostPath='" + hostPath + '\'' +
                ", containerPath='" + containerPath + '\'' +
                '}';
    }
}
