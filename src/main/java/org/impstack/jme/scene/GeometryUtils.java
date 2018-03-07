package org.impstack.jme.scene;

import com.jme3.app.Application;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GeometryUtils {

    private static final Logger LOG = LoggerFactory.getLogger(GeometryUtils.class);
    private static final String DEBUG_MATERIAL = "Common/MatDefs/Misc/Unshaded.j3md";
    private static final String DEBUG_MATERIAL_COLOR = "Color";

    private final Application application;

    public GeometryUtils(Application application) {
        this.application = application;
    }

    public Geometry createGeometry(Mesh mesh) {
        return createGeometry(mesh.toString(), mesh, ColorRGBA.Blue, false);
    }

    public Geometry createGeometry(Mesh mesh, ColorRGBA color) {
        return createGeometry(mesh.toString(), mesh, color, false);
    }

    public Geometry createGeometry(String name, Mesh mesh, ColorRGBA color, boolean wireframe) {
        Geometry geometry = new Geometry(name, mesh);
        Material material = new Material(application.getAssetManager(), DEBUG_MATERIAL);
        material.getAdditionalRenderState().setWireframe(wireframe);
        material.setColor(DEBUG_MATERIAL_COLOR, color);
        material.getAdditionalRenderState().setLineWidth(1);
        if (color.getAlpha() != 1) {
            material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            geometry.setQueueBucket(RenderQueue.Bucket.Transparent);
        }
        geometry.setMaterial(material);
        return geometry;
    }
}
