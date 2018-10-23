package org.impstack.jme.es;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityContainer;
import com.simsilica.es.EntityData;
import org.impstack.jme.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * An application state that handles visual entities.
 * It also provides a queue system to attach objects to the scene graph. Only one object is attached each frame.
 * Attaching a lot of objects to the scenegraph has an impact on performance.
 * <p>
 * For entities to be picked up by the visual state, they should have a {@link Model} and {@link Position} component.
 */
public class VisualState extends BaseAppState {

    private static final Logger LOG = LoggerFactory.getLogger(VisualState.class);

    private final EntityData entityData;
    private final Queue<Spatial> attachQueue = new ConcurrentLinkedQueue<>();

    private Node sceneGraph;
    private ModelRegistry modelRegistry;
    private EntityContainer<Spatial> models;

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
    }

    @Override
    protected void onEnable() {
        models.start();
    }

    @Override
    public void update(float tpf) {
        models.update();

        // attach objects
        if (!attachQueue.isEmpty()) {
            sceneGraph.attachChild(attachQueue.poll());
        }
    }

    @Override
    protected void onDisable() {
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

    private class ModelContainer extends EntityContainer<Spatial> {

        public ModelContainer(EntityData ed) {
            super(ed, Model.class, Position.class);
        }

        @Override
        protected Spatial addObject(Entity e) {
            Model model = e.get(Model.class);
            Position position = e.get(Position.class);

            Spatial spatial = modelRegistry.get(model);
            spatial.setLocalTranslation(position.getLocation());
            spatial.setLocalRotation(position.getRotation());

            LOG.trace("Attach {} on {}", spatial, sceneGraph);
            attach(spatial);

            return spatial;
        }

        @Override
        protected void updateObject(Spatial object, Entity e) {
            Position position = e.get(Position.class);

            object.setLocalTranslation(position.getLocation());
            object.setLocalRotation(position.getRotation());
        }

        @Override
        protected void removeObject(Spatial object, Entity e) {
            LOG.trace("Remove {} from {}", object, object.getParent());
            object.removeFromParent();
        }

    }

}
