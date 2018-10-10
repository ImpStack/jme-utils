package org.impstack.jme.es;

import com.simsilica.es.EntityComponent;

/**
 * A Decay entity component that is handled by the {@link DecaySystem}. Additional a duration and start time can be
 * specified on the component.
 */
public class Decay implements EntityComponent {

    private final long startTime;
    private final long duration;

    public Decay() {
        this(0, 0);
    }

    public Decay(long duration) {
        this(System.currentTimeMillis(), duration);
    }

    public Decay(long startTime, long duration) {
        this.startTime = startTime;
        this.duration = duration;
    }

    public long getTimeRemaining() {
        return Math.max(0, (startTime + duration) - System.currentTimeMillis());
    }

    public double getPercentRemaining() {
        if (System.currentTimeMillis() >= startTime + duration) {
            return 0;
        }

        long remaining = (startTime + duration) - System.currentTimeMillis();
        return remaining / (double) duration;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= startTime + duration;
    }

    @Override
    public String toString() {
        return "Decay{" +
                "startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }
}
