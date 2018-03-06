package org.impstack.jme.scene;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

import java.util.Optional;

/**
 * Utility class for spatial objects
 *
 * @author remy
 * @since 11/10/17
 */
public final class SpatialUtils {

    /**
     * Perform a depthfirst traversal of the spatial, to locate the first control of the given type.
     *
     * @param spatial: the scene graph to traverse
     * @param <T> type: the class of the control
     * @return the first control that matches the given class, or null if no control is found.
     */
    public static <T extends Control> T findControl(Spatial spatial, Class<T> type) {
        if (spatial.getControl(type) != null) {
            return spatial.getControl(type);
        } else {
            if (spatial instanceof Node) {
                for (Spatial s : ((Node) spatial).getChildren()) {
                    T control = findControl(s, type);
                    if (control != null) {
                        return control;
                    }
                }
            }
        }
        return null;
    }

    public static <T extends Control> Optional<T> getControl(Spatial spatial, Class<T> type) {
        return Optional.ofNullable(findControl(spatial, type));
    }

    /**
     * Perform a depthfirst traversal of the spatial, to locate the first geometry.
     * @param spatial : the scene graph to traverse
     * @return the first geometry, or null if no geometry is found.
     */
    public static Geometry findGeometry(Spatial spatial) {
        if (spatial instanceof Geometry) {
            return (Geometry) spatial;
        } else {
            if (spatial instanceof Node) {
                for (Spatial s : ((Node) spatial).getChildren()) {
                    Geometry geometry = findGeometry(s);
                    if (geometry != null) {
                        return geometry;
                    }
                }
            }
        }
        return null;
    }

    public static Optional<Geometry> getGeometry(Spatial spatial) {
        return Optional.ofNullable(findGeometry(spatial));
    }
}