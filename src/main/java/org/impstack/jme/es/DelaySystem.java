package org.impstack.jme.es;

import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;
import com.simsilica.sim.AbstractGameSystem;
import com.simsilica.sim.SimTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link com.simsilica.sim.GameSystem} implementation that handles {@link Delay} components.
 * When the duration of the {@link Delay} entity component is expired, the entity components are added to the entity.
 */
public class DelaySystem extends AbstractGameSystem {

    private static final Logger LOG = LoggerFactory.getLogger(DelaySystem.class);

    private EntityData entityData;
    private EntitySet delayedComponents;

    public DelaySystem() {
    }

    public DelaySystem(EntityData entityData) {
        this.entityData = entityData;
    }

    @Override
    protected void initialize() {
        delayedComponents = entityData.getEntities(Delay.class);
    }

    @Override
    public void update(SimTime time) {
        delayedComponents.applyChanges();
        delayedComponents.forEach(e -> {
            Delay delay = e.get(Delay.class);
            if (delay.isExpired()) {
                LOG.trace("Setting {} on {}", delay.getComponents(), e.getId());
                entityData.setComponents(e.getId(), delay.getComponents());
                entityData.removeComponent(e.getId(), Delay.class);
            }
        });
    }

    @Override
    protected void terminate() {
        delayedComponents.release();
    }

    public void setEntityData(EntityData entityData) {
        this.entityData = entityData;
    }

}
