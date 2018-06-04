package com.github.lkq.smesh.test.benchmark;

public class Reporter {
    public static final int NANO_TO_MILLS = 1000000;
    private long count;
    private long nanoTime;

    public synchronized void report(long nanoTime) {
        this.count++;
        this.nanoTime += nanoTime;
    }

    public String report() {
        return "{\"total\": " + nanoTime + " ns, \"count\": " + count +", \"avg\": " + (count > 0 ? nanoTime / count : 0) +" ns}";
    }
}
