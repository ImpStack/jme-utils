package org.impstack.jme.es;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A base model registry implementation that uses an internal thread-safe index to look up models.
 * The {@link #getModelPath(Model)} method can be overwritten to allow for custom path retrieval for models that aren't
 * found in the registry.
 */
public class BaseModelRegistry implements ModelRegistry {

    protected static final Logger LOG = LoggerFactory.getLogger(BaseModelRegistry.class);

    protected final Map<String, String> registry = new ConcurrentHashMap<>();
    protected final AssetManager assetManager;

    public BaseModelRegistry(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public String register(Model model, String modelPath) {
        registry.put(model.getModelId(), modelPath);
        LOG.trace("Registering {} -> {}", model, modelPath);
        return modelPath;
    }

    @Override
    public Spatial get(Model model) {
        String path = registry.get(model.getModelId());
        if (path != null) {
            LOG.trace("Retrieving {} -> {}", model, path);
            return assetManager.loadModel(path);
        }

        // model isn't found in the registry. Use the custom getModelPath method
        path = getModelPath(model);
        if (path == null) {
            throw new IllegalArgumentException("No model could be retrieved for " + model);
        }

        return assetManager.loadModel(register(model, path));
    }

    protected String getModelPath(Model model) {
        return null;
    }

}
