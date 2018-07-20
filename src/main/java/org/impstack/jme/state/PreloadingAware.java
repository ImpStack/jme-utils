package org.impstack.jme.state;

import com.jme3.scene.Spatial;

/**
 * All classes that implement this method have the ability to preload the scene. Preloading a scene is to preload the
 * data to the gpu so that it doesn't need to be done on the first render.
 * It is done by calling {@link com.jme3.renderer.RenderManager#preloadScene(Spatial)} method. This should ALWAYS
 * be called from the render thread! This can still cause a hickup, but you can control when it happens.
 * @author remy
 * @since 20/07/18.
 */
public interface PreloadingAware {

    public void preloadScene();

}
