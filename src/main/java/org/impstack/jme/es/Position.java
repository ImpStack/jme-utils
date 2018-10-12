package org.impstack.jme.es;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * An entity component specifying the position of an entity. A position has a {@link com.jme3.math.Vector3f} location
 * and a {@link com.jme3.math.Quaternion} rotation.
 */
public class Position implements EntityComponent {

    private final Vector3f location;
    private final Quaternion rotation;

    public Position() {
        this(new Vector3f(), new Quaternion());
    }

    public Position(Vector3f location) {
        this(location, new Quaternion());
    }

    public Position(Quaternion rotation) {
        this(new Vector3f(), rotation);
    }

    public Position(Vector3f location, Quaternion rotation) {
        this.location = location;
        this.rotation = rotation;
    }

    public Vector3f getLocation() {
        return location;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    @Override
    public String toString() {
        return "Position{" +
                "location=" + location +
                ", rotation=" + rotation +
                '}';
    }
}
