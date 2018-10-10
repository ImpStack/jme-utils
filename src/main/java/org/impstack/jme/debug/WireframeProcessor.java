package org.impstack.jme.debug;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.post.SceneProcessor;
import com.jme3.profile.AppProfiler;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.texture.FrameBuffer;

/**
 * A {@link SceneProcessor} that forces a wireframe material on all the objects in the scene.
 * The color of the wireframe can be set using the {@link #setColor(ColorRGBA)} method.
 * The width of the lines of the wireframe can be set using the {@link #setLineWidth(float)} method.
 */
public class WireframeProcessor implements SceneProcessor {

    private static final String WIREFRAME_MATERIAL = "Materials/Wireframe.j3m";

    private Material wireframeMaterial;
    private RenderManager renderManager;

    public WireframeProcessor(AssetManager assetManager) {
        wireframeMaterial = assetManager.loadMaterial(WIREFRAME_MATERIAL);
    }

    @Override
    public void initialize(RenderManager rm, ViewPort vp) {
        renderManager = rm;
    }

    @Override
    public void reshape(ViewPort vp, int w, int h) {
    }

    @Override
    public boolean isInitialized() {
        return renderManager != null;
    }

    @Override
    public void preFrame(float tpf) {
    }

    @Override
    public void postQueue(RenderQueue rq) {
        renderManager.setForcedMaterial(wireframeMaterial);
    }

    @Override
    public void postFrame(FrameBuffer out) {
        renderManager.setForcedMaterial(null);
    }

    @Override
    public void cleanup() {
        renderManager.setForcedMaterial(null);
    }

    @Override
    public void setProfiler(AppProfiler profiler) {
    }

    /**
     * Set a custom wireframe color. The default color is (RGBA: 0.2 0.4 1.0 1.0)
     * @param color the color value
     */
    public void setColor(ColorRGBA color) {
        wireframeMaterial.setColor("Color", color);
    }

    /**
     * Set a custom line width. The default line width is 1.0f
     * @param width the width of the line
     */
    public void setLineWidth(float width) {
        wireframeMaterial.getAdditionalRenderState().setLineWidth(width);
    }

    @Override
    public String toString() {
        return "WireframeProcessor{" +
                "color=" + wireframeMaterial.getParam("Color").getValue() +
                ", lineWidth=" + wireframeMaterial.getAdditionalRenderState().getLineWidth() +
                '}';
    }
}
