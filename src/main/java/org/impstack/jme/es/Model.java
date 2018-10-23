package org.impstack.jme.es;

import com.simsilica.es.EntityComponent;

/**
 * An entity component specifying a model id of an entity.
 */
public class Model implements EntityComponent {

    private final String modelId;

    public Model(String modelId) {
        this.modelId = modelId;
    }

    public String getModelId() {
        return modelId;
    }

    @Override
    public String toString() {
        return "Model{" +
                "modelId='" + modelId + '\'' +
                '}';
    }

}
