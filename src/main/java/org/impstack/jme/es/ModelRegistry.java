package org.impstack.jme.es;

import com.jme3.scene.Spatial;

public interface ModelRegistry {

    /**
     * Register a spatial with the model component.
     * @param model the model component holding the key
     * @param path the path inside the assets folder for the model
     * @return the path to the model
     */
    public String register(Model model, String path);

    /**
     * Returns the spatial linked to the model component.
     * @param model the model component holding the key
     * @return the spatial linked to the model component
     */
    public Spatial get(Model model);

}
