package org.impstack.jme.shadow;

import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import org.impstack.jme.JmeLauncher;

public class ShadowTest extends JmeLauncher {

    public static void main(String[] args) {
        new ShadowTest().start();
    }

    @Override
    public void init() {
        Geometry sphere = new Geometry("Cylinder", new Sphere(32, 32, 1));
        Material sphereMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        sphereMaterial.setBoolean("UseMaterialColors", true);
        sphereMaterial.setColor("Diffuse", ColorRGBA.Green);
        sphereMaterial.setColor("Ambient", ColorRGBA.Green);
        sphereMaterial.setColor("Specular", ColorRGBA.White);
        sphereMaterial.setFloat("Shininess", 1f);
        sphere.setMaterial(sphereMaterial);

        Geometry floor = new Geometry("Floor", new Quad(4, 4));
        Material floorMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        floorMaterial.setBoolean("UseMaterialColors", true);
        floorMaterial.setColor("Diffuse", ColorRGBA.LightGray);
        floorMaterial.setColor("Ambient", ColorRGBA.LightGray);
        floorMaterial.setColor("Specular", ColorRGBA.Black);
        floorMaterial.setFloat("Shininess", 0f);
        floor.setMaterial(floorMaterial);

        sphere.setLocalTranslation(0, 0, 0);
        floor.setLocalTranslation(-2f, -1.5f, 2f);
        floor.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));

        getRootNode().attachChild(sphere);
        getRootNode().attachChild(floor);

        getRootNode().addLight(new AmbientLight(ColorRGBA.White.mult(0.2f)));
        getRootNode().addLight(new DirectionalLight(new Vector3f(-0.3f, -1, 0).normalizeLocal(), ColorRGBA.White));

        FilterPostProcessor filterPostProcessor = new FilterPostProcessor(assetManager);
        filterPostProcessor.addFilter(new DropShadowFilter());

        getViewPort().addProcessor(filterPostProcessor);
    }
}
