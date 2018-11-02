package org.impstack.jme.es;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.*;
import com.simsilica.es.EntityContainer;
import org.impstack.jme.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * An application state that handles visual entities.
 * It also provides a queue system to attach objects to the scene graph. Only one object is attached each frame.
 * Attaching a lot of objects to the scenegraph has an impact on performance.
 * <p>
 * All entities with a {@link Model} component will be handled and loaded by the {@link ModelRegistry}.
 * Loaded models can be retrieved by using {@link #getModel(EntityId)}.
 * <p>
 * Entities will be directly added to the given scene graph when they also have a {@link Position} component next to the
 * {@link Model} component.
 */
public class VisualState extends BaseAppState {

    private static final Logger LOG = LoggerFactory.getLogger(VisualState.class);

    private final EntityData entityData;
    private final Queue<Spatial> attachQueue = new ConcurrentLinkedQueue<>();

    private Node sceneGraph;
    private ModelRegistry modelRegistry;
    private EntityContainer<Spatial> models;
    private EntitySet attachedModels;

    public VisualState(EntityData entityData) {
        this.entityData = entityData;
    }

    public VisualState(EntityData entityData, Node sceneGraph) {
        this.entityData = entityData;
        this.sceneGraph = sceneGraph;
    }

    public VisualState(EntityData entityData, ModelRegistry modelRegistry) {
        this.entityData = entityData;
        this.modelRegistry = modelRegistry;
    }

    public VisualState(EntityData entityData, Node sceneGraph, ModelRegistry modelRegistry) {
        this.entityData = entityData;
        this.sceneGraph = sceneGraph;
        this.modelRegistry = modelRegistry;
    }

    @Override
    protected void initialize(Application app) {
        if (modelRegistry == null)
            throw new IllegalArgumentException("ModelRegistry is not defined when initializing VisualState");

        if (sceneGraph == null) {
            sceneGraph = ApplicationContext.INSTANCE.getRootNode();
        }

        models = new ModelContainer(entityData);
        attachedModels = entityData.getEntities(Model.class, Position.class);
    }

    @Override
    protected void onEnable() {
        // start the model container
        models.start();

        // start the attached model entity set
        attachedModels.applyChanges();
        addAttachedEntities(attachedModels);
    }

    @Override
    public void update(float tpf) {
        models.update();

        // update the attached model entity set
        if (attachedModels.applyChanges()) {
            removeAttachedEntities(attachedModels.getRemovedEntities());
            addAttachedEntities(attachedModels.getAddedEntities());
            updateAttachedEntities(attachedModels.getChangedEntities());
        }

        // attach objects
        if (!attachQueue.isEmpty()) {
            sceneGraph.attachChild(attachQueue.poll());
        }
    }

    @Override
    protected void onDisable() {
        // stop attached model entity set
        removeAttachedEntities(attachedModels);
        attachedModels.release();

        // clean up all models
        models.stop();
    }

    @Override
    protected void cleanup(Application app) {
    }

    public void attach(Spatial spatial) {
        attachQueue.offer(spatial);
    }

    public Node getSceneGraph() {
        return sceneGraph;
    }

    public void setSceneGraph(Node sceneGraph) {
        this.sceneGraph = sceneGraph;
    }

    public ModelRegistry getModelRegistry() {
        return modelRegistry;
    }

    public void setModelRegistry(ModelRegistry modelRegistry) {
        this.modelRegistry = modelRegistry;
    }

    public Spatial getModel(EntityId entityId) {
        return models.getObject(entityId);
    }

    private void addAttachedEntities(Set<Entity> addedEntities) {
        addedEntities.forEach(e -> {
            Position position = e.get(Position.class);

            Spatial spatial = getModel(e.getId());
            spatial.setLocalTranslation(position.getLocation());
            spatial.setLocalRotation(position.getRotation());

            LOG.trace("Attach {} on {}", spatial, sceneGraph);
            attach(spatial);
        });
    }

    private void updateAttachedEntities(Set<Entity> changedEntities) {
        changedEntities.forEach(e -> {
            Position position = e.get(Position.class);

            Spatial spatial = getModel(e.getId());
            spatial.setLocalTranslation(position.getLocation());
            spatial.setLocalRotation(position.getRotation());
        });
    }

    private void removeAttachedEntities(Set<Entity> removedEntities) {
        removedEntities.forEach(e -> {
            Spatial spatial = getModel(e.getId());
            LOG.trace("Remove {} from {}", spatial, spatial.getParent());
            spatial.removeFromParent();
        });
    }

    private class ModelContainer extends EntityContainer<Spatial> {

        public ModelContainer(EntityData ed) {
            super(ed, Model.class);
        }

        @Override
        protected Spatial addObject(Entity e) {
            Model model = e.get(Model.class);
            return modelRegistry.get(model);
        }

        @Override
        protected void updateObject(Spatial object, Entity e) {
        }

        @Override
        protected void removeObject(Spatial object, Entity e) {
        }

    }

}
