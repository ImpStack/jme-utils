package org.impstack.jme.es;

import com.simsilica.es.EntityComponent;

import java.util.Arrays;

/**
 * An entity component holding a duration and a list of entity components that is handled by the {@link DelaySystem}.
 * The entity components are added to the entity after the given duration.
 */
public class Delay implements EntityComponent {

    private final EntityComponent[] components;
    private final long startTime;
    private final long duration;

    public Delay(EntityComponent... components) {
        this(0, components);
    }

    public Delay(long duration, EntityComponent... components) {
        this.duration = duration;
        this.components = components;
        this.startTime = System.currentTimeMillis();
    }

    public EntityComponent[] getComponents() {
        return components;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= startTime + duration;
    }

    @Override
    public String toString() {
        return "Delay{" +
                "components=" + Arrays.toString(components) +
                ", duration=" + duration +
                '}';
    }

}
