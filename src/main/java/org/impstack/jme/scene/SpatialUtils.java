package org.impstack.jme.scene;

import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bounding.BoundingVolume;
import com.jme3.math.Vector3f;
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
     * @param spatial the scene graph to traverse
     * @param type the class of the control
     * @param <T> the type of the control
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

    /**
     * Returns the extent vector of a bounding volume. For a bounding box this is the normal extent vector from the
     * center, for a bounding sphere this is a vector holding the radius.
     * @param bv the bounding volume
     * @return a vector containing the extent
     */
    public static Vector3f getExtent(BoundingVolume bv) {
        if (bv instanceof BoundingBox) {
            return ((BoundingBox) bv).getExtent(null);
        } else if (bv instanceof BoundingSphere) {
            return new Vector3f(((BoundingSphere) bv).getRadius(), ((BoundingSphere) bv).getRadius(), ((BoundingSphere) bv).getRadius());
        } else {
            throw new IllegalArgumentException("Unable to determine bounding volume type: " + bv);
        }
    }

    /**
     * Returns the largest extent value of a bounding volume. For a bounding box this is the highest value for the axis
     * for a bounding sphere this is the radius.
     * @param bv the bounding volume
     * @return the highest extent axis
     */
    public static float getMaxExtent(BoundingVolume bv) {
       if (bv instanceof BoundingBox) {
           Vector3f extent = ((BoundingBox) bv).getExtent(null);
           return Math.max(extent.x, Math.max(extent.y, extent.z));
       } else if (bv instanceof BoundingSphere) {
           return ((BoundingSphere) bv).getRadius();
       } else {
           throw new IllegalArgumentException("Unable to determine bounding volume type: " + bv);
       }
    }
}