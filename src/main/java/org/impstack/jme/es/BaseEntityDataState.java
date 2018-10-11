package org.impstack.jme.es;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.simsilica.es.EntityData;
import com.simsilica.es.base.DefaultEntityData;

/**
 * An Application state that handles the lifecycle of an {@link EntityData} set.
 * When the state is detached, the {@link EntityData} set is closed.
 */
public class BaseEntityDataState extends BaseAppState {

    private final EntityData entityData;

    public BaseEntityDataState() {
        this(new DefaultEntityData());
    }

    public BaseEntityDataState(EntityData entityData) {
        this.entityData = entityData;
    }

    @Override
    protected void initialize(Application app) {
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

    @Override
    protected void cleanup(Application app) {
        entityData.close();
    }

    public EntityData getEntityData() {
        return entityData;
    }

}
