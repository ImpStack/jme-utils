package org.impstack.jme.state;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An AppState that handles the lifecycle of a viewport
 * By default buffer clearing is enabled for the color buffer, depth buffer and stencil buffer.
 */
public class ViewPortState extends BaseAppState {

    private static final Logger LOG = LoggerFactory.getLogger(ViewPortState.class);

    public enum RenderOrder {
        PREVIEW, MAINVIEW, POSTVIEW;
    }

    private final String name;
    private final Camera camera;
    private final Spatial scene;
    private final RenderOrder order;
    private ViewPort viewPort;

    public ViewPortState(Camera camera, Spatial scene) {
        this(camera.getName(), camera, scene, RenderOrder.POSTVIEW);
    }

    public ViewPortState(Camera camera, Spatial scene, RenderOrder order) {
        this(camera.getName(), camera, scene, order);
    }

    public ViewPortState(String name, Camera camera, Spatial scene, RenderOrder order) {
        this.name = name;
        this.camera = camera;
        this.scene = scene;
        this.order = order;
    }

    @Override
    protected void initialize(Application app) {
        switch (order) {
            case PREVIEW:
                viewPort = getApplication().getRenderManager().createPreView(name, camera);
                break;
            case MAINVIEW:
                viewPort = getApplication().getRenderManager().createMainView(name, camera);
                break;
            case POSTVIEW:
                viewPort = getApplication().getRenderManager().createPostView(name, camera);
                break;
        }

        viewPort.setClearFlags(true, true, true);
        viewPort.attachScene(scene);

        LOG.debug("Created viewport {} for rendering with attached scene {}", viewPort, scene);
    }

    @Override
    protected void onEnable() {
    }

    @Override
    public void update(float tpf) {
        scene.updateLogicalState(tpf);
        scene.updateGeometricState();
    }

    @Override
    protected void onDisable() {
    }

    @Override
    protected void cleanup(Application app) {
        viewPort.clearScenes();

        switch (order) {
            case PREVIEW:
                getApplication().getRenderManager().removePreView(viewPort);
                break;
            case MAINVIEW:
                getApplication().getRenderManager().removeMainView(viewPort);
                break;
            case POSTVIEW:
                getApplication().getRenderManager().removePostView(viewPort);
                break;
        }

        LOG.debug("Detached scene {} and removed viewport {} for rendering", scene, viewPort);
    }

    public ViewPort getViewPort() {
        return viewPort;
    }

    public Spatial getScene() {
        return scene;
    }

    /**
     * A helper method to set the size and location of the viewport. Where (0,0) is the bottom left corner of the
     * screen, and (camera.width, camera.height) is the upper right corner of the screen.
     * The viewport is positioned by the bottom left corner and the size of the viewport grows the viewport up and right.
     * @param width width of the viewport
     * @param height height of the viewport
     * @param location location of the viewport
     * @return the viewport state
     */
    public ViewPortState setSize(int width, int height, Vector2f location) {
        float w = camera.getWidth();
        float h = camera.getHeight();
        camera.setViewPort(1 / w * location.x, 1 / w * (location.x + width), 1 / h * location.y, 1 / h * (location.y + height));
        LOG.debug("Setting viewport location: (x1, x2, y1, y2) ({}, {}, {}, {})", 1 / w * location.x, 1 / w * (location.x + width), 1 / h * location.y, 1 / h * (location.y + height));
        return this;
    }

}
