package org.impstack.time;

/**
 * A simple timer class, with a milliseconds accuracy.
 * This timer is NOT threadsafe!
 */
public final class Timer {

    private long start;

    public Timer() {
        start = System.currentTimeMillis();
    }

    public long read() {
        return System.currentTimeMillis() - start;
    }

    public long reset() {
        long time = read();
        start = System.currentTimeMillis();
        return time;
    }
}
