package org.impstack.jme.state;

import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Spatial;

/**
 * An application state to handle constructing, loading and attaching/detaching scenes.
 * The user can choose when to preload, attach and detach the scene.
 * Preloading a scene is to preload the data to the gpu so that it doesn't need to be done on the first render.
 * It is done by calling {@link com.jme3.renderer.RenderManager#preloadScene(Spatial)} method. This should ALWAYS
 * be called from the render thread! This can still cause a hiccup, but you can control when it happens.
 * @author remy
 * @since 20/07/18.
 */
public abstract class BaseSceneState extends BaseAppState {

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

    /**
     * Specifies if the screen is ready for preloading.
     * @return true if the scene can be preloaded or attached
     */
    public abstract boolean isSceneConstructed();

    /**
     * Preload the data to the GPU. This should ALWAYS be called from the render thread.
     */
    public abstract void preloadScene();

    /**
     * Attach the scene
     */
    public abstract void attachScene();

    /**
     * Detach the scene
     */
    public abstract void detachScene();



}
