package org.impstack.jme.state;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
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

    enum RenderOrder {
        PREVIEW, MAINVIEW, POSTVIEW;
    }

    private final String name;
    private final Camera camera;
    private final Spatial spatial;
    private final RenderOrder order;
    private ViewPort viewPort;

    public ViewPortState(Camera camera, Spatial spatial, RenderOrder order) {
        this(camera.getName(), camera, spatial, order);
    }

    public ViewPortState(String name, Camera camera, Spatial spatial, RenderOrder order) {
        this.name = name;
        this.camera = camera;
        this.spatial = spatial;
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
        viewPort.attachScene(spatial);

        LOG.info("Created viewport {} for rendering with attached scene {}", name, spatial);
    }

    @Override
    protected void onEnable() {
    }

    @Override
    public void update(float tpf) {
        spatial.updateLogicalState(tpf);
        spatial.updateGeometricState();
    }

    @Override
    protected void onDisable() {
    }

    @Override
    protected void cleanup(Application app) {
        viewPort.detachScene(spatial);

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

        LOG.info("Detached scene {} and removed viewport {} for rendering", spatial, viewPort);
    }

    public ViewPort getViewPort() {
        return viewPort;
    }

}
