package org.impstack.jme.es;

import com.simsilica.es.Entity;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

import java.util.Set;

/**
 * An abstract base class that implements a lot of boilerplate code to query and update an entity set.
 * Deprecated, use {@link com.simsilica.es.EntityContainer}
 */
@Deprecated
public abstract class EntityContainer {

    private final EntityData entityData;
    private final Class<? extends EntityComponent> [] componentTypes;
    private EntitySet entities;

    public EntityContainer(EntityData entityData, Class<? extends EntityComponent>... componentTypes) {
        this.entityData = entityData;
        this.componentTypes = componentTypes;
    }

    public void start() {
        entities = entityData.getEntities(componentTypes);
        entities.applyChanges();
        addEntities(entities);
    }

    public boolean update() {
        if (entities.applyChanges()) {
            addEntities(entities.getAddedEntities());
            changeEntities(entities.getChangedEntities());
            removeEntities(entities.getRemovedEntities());
            return true;
        }
        return false;
    }

    public void stop() {
        removeEntities(entities);
        entities.release();
    }

    public EntityData getEntityData() {
        return entityData;
    }

    public EntitySet getEntities() {
        return entities;
    }

    public int size() {
        return entities.size();
    }

    protected abstract void addEntity(Entity e);

    protected abstract void updateEntity(Entity e);

    protected abstract void removeEntity(Entity e);

    private void addEntities(Set<Entity> set) {
        if (set.isEmpty()) {
            return;
        }

        set.forEach(this::addEntity);
    }

    private void changeEntities(Set<Entity> set) {
        if (set.isEmpty()) {
            return;
        }

        set.forEach(this::updateEntity);
    }

    private void removeEntities(Set<Entity> set) {
        if (set.isEmpty()) {
            return;
        }

        set.forEach(this::removeEntity);
    }
}
