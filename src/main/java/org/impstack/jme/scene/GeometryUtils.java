package org.impstack.jme.scene;

import com.jme3.app.Application;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GeometryUtils {

    private static final Logger LOG = LoggerFactory.getLogger(GeometryUtils.class);
    private static final String DEBUG_MATERIAL = "Common/MatDefs/Misc/Unshaded.j3md";
    private static final String DEBUG_MATERIAL_COLOR = "Color";
    private static final String DEBUG_MATERIAL_TEXTURE = "ColorMap";

    private final Application application;

    public GeometryUtils(Application application) {
        this.application = application;
    }

    /**
     * Creates a geometry of the given mesh and an unshaded material with a {@code ColorRGBA.Blue} color
     * @param mesh the mesh
     * @return a geometry
     */
    public Geometry createGeometry(Mesh mesh) {
        return createGeometry(mesh.toString(), mesh, ColorRGBA.Blue, false);
    }

    /**
     * Create a geometry of the given mesh and an ushaded material with the given color.
     * @param mesh the mesh
     * @param color the color of the material
     * @return a geometry
     */
    public Geometry createGeometry(Mesh mesh, ColorRGBA color) {
        return createGeometry(mesh.toString(), mesh, color, false);
    }

    /**
     * Create a geometry of the given mesh and an unshaded material with the given texture.
     * @param mesh the mesh
     * @param texture the texture of the material
     * @return a geometry
     */
    public Geometry createGeometry(Mesh mesh, String texture) {
        Geometry geometry = createGeometry(mesh, ColorRGBA.White);
        geometry.getMaterial().setTexture(DEBUG_MATERIAL_TEXTURE, application.getAssetManager().loadTexture(texture));
        return geometry;
    }

    /**
     * Creates a geometry of the given mesh and an unshaded material with the given color
     * @param name name of the geometry
     * @param mesh the mesh
     * @param color the color of the material
     * @param wireframe toggle wireframe renderstate
     * @return a geometry
     */
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

    /**
     * Creates a node with 3 arrows, that represent the coordinate axes. Where the red arrow represents the X axis,
     * the green arrow represents the Y axis and the blue arrow represents the Z axis.
     * @return the node holding the 3 arrows
     */
    public Node createCoordinateAxes() {
        Node node = new Node("Coordinate axes");
        node.attachChild(createGeometry("X-axis", new Arrow(Vector3f.UNIT_X), ColorRGBA.Red, false));
        node.attachChild(createGeometry("Y-axis", new Arrow(Vector3f.UNIT_Y), ColorRGBA.Green, false));
        node.attachChild(createGeometry("Z-axis", new Arrow(Vector3f.UNIT_Z), ColorRGBA.Blue, false));
        return node;
    }

}
