package org.impstack.jme.es;

import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;
import com.simsilica.sim.AbstractGameSystem;
import com.simsilica.sim.SimTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link com.simsilica.sim.GameSystem} implementation that handles {@link Decay} entity components.
 * When the duration on the {@link Decay} entity component is expired, the entity is removed from the entityData set.
 */
public class DecaySystem extends AbstractGameSystem {

    private static final Logger LOG = LoggerFactory.getLogger(DecaySystem.class);

    private EntityData entityData;
    private EntitySet decayEntities;

    public DecaySystem() {
    }

    public DecaySystem(EntityData entityData) {
        this.entityData = entityData;
    }

    @Override
    protected void initialize() {
        decayEntities = entityData.getEntities(Decay.class);
    }

    @Override
    protected void terminate() {
        decayEntities.release();
    }

    @Override
    public void update(SimTime time) {
        decayEntities.applyChanges();
        decayEntities.forEach(entity -> {
            Decay decay = entity.get(Decay.class);
            if (decay.isDead()) {
                entityData.removeEntity(entity.getId());
                LOG.trace("Removing entity {}", entity);
            }
        });
    }

    public void setEntityData(EntityData entityData) {
        this.entityData = entityData;
    }
}
