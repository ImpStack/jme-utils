package org.impstack.jme.es;

import com.jme3.scene.Spatial;

public interface ModelRegistry {

    /**
     * Register a spatial with the model component.
     * @param model the model component holding the key
     * @param spatial the spatial
     * @return the spatial linked to the model component
     */
    public Spatial register(Model model, Spatial spatial);

    /**
     * Returns the spatial linked to the model component.
     * @param model the model component holding the key
     * @return the spatial linked to the model component
     */
    public Spatial get(Model model);

}
