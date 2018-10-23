package org.impstack.jme.es;

import com.jme3.scene.Spatial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A base model registry implementation that uses an internal thread-safe index to look up models.
 * The load method can be overwritten to allow for custom load behaviour for models that aren't found in the
 * registry.
 */
public class BaseModelRegistry implements ModelRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(BaseModelRegistry.class);

    private final Map<String, Spatial> registry = new ConcurrentHashMap<>();

    @Override
    public Spatial register(Model model, Spatial spatial) {
        registry.put(model.getModelId(), spatial);
        LOG.trace("Registering {} -> {}", model, spatial);
        return spatial;
    }

    @Override
    public Spatial get(Model model) {
        Spatial spatial = registry.get(model.getModelId());
        if (spatial != null) {
            LOG.trace("Retrieving {} -> {}", model, spatial);
            return spatial;
        }

        // model isn't found in the registry. Use the custom loadSpatial() method.
        spatial = loadSpatial(model);
        if (spatial == null) {
            throw new IllegalArgumentException("No model could be retrieved for " + model);
        }

        return register(model, spatial);
    }

    protected Spatial loadSpatial(Model model) {
        return null;
    }
}
