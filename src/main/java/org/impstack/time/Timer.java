package org.impstack.time;

import java.util.concurrent.TimeUnit;

/**
 * A simple timer class, with a nanoseconds accuracy.
 * This timer is NOT threadsafe!
 */
public final class Timer {

    private static final long NANO_2_MILLIS = 1000000L;

    private long start;

    public Timer() {
        start = System.nanoTime();
    }

    /**
     * @return the elapsed time in milliseconds
     */
    public long read() {
        return getNanoTime() / NANO_2_MILLIS;
    }

    public long read(TimeUnit timeUnit) {
        return timeUnit.convert(getNanoTime(), TimeUnit.NANOSECONDS);
    }

    public long reset() {
        long time = read();
        start = System.nanoTime();
        return time;
    }

    private long getNanoTime() {
        return System.nanoTime() - start;
    }
}
