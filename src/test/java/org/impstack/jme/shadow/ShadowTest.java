package org.impstack.jme.shadow;

import com.jme3.app.FlyCamAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import org.impstack.jme.JmeLauncher;
import org.slf4j.LoggerFactory;

/**
 * A test case for the {@link DropShadowFilter}
 * When running you can use the following keys to adapt the shadow settings:
 * - key 'o' : increase/decrease the shadow intensity
 * - key 'l' : adapt the height of the floor
 */
public class ShadowTest extends JmeLauncher implements ActionListener {

    private float shadowIntensity = 0.75f;
    private DropShadowFilter dropShadowFilter;
    private Geometry floor;

    public static void main(String[] args) {
        new ShadowTest().start();
    }

    @Override
    public void init() {
        Geometry sphere = new Geometry("Sphere", new Sphere(32, 32, 1));
        Material sphereMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        sphereMaterial.setBoolean("UseMaterialColors", true);
        sphereMaterial.setColor("Diffuse", ColorRGBA.Green);
        sphereMaterial.setColor("Ambient", ColorRGBA.Green);
        sphereMaterial.setColor("Specular", ColorRGBA.White);
        sphereMaterial.setFloat("Shininess", 1f);
        sphere.setMaterial(sphereMaterial);
        sphere.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        floor = new Geometry("Floor", new Quad(4, 4));
        Material floorMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        floorMaterial.setBoolean("UseMaterialColors", true);
        floorMaterial.setColor("Diffuse", ColorRGBA.LightGray);
        floorMaterial.setColor("Ambient", ColorRGBA.LightGray);
        floorMaterial.setColor("Specular", ColorRGBA.Black);
        floorMaterial.setFloat("Shininess", 0f);
        floor.setMaterial(floorMaterial);

        sphere.setLocalTranslation(0, 0, 0);
        floor.setLocalTranslation(-2f, -1f, 2f);
        floor.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));

        getRootNode().attachChild(sphere);
        getRootNode().attachChild(floor);

        getRootNode().addLight(new AmbientLight(ColorRGBA.White.mult(0.2f)));
        getRootNode().addLight(new DirectionalLight(new Vector3f(-0.3f, -1, 0).normalizeLocal(), ColorRGBA.White));

        FilterPostProcessor filterPostProcessor = new FilterPostProcessor(assetManager);
        filterPostProcessor.setNumSamples(1);
        dropShadowFilter = new DropShadowFilter();
        dropShadowFilter.setShadowIntensity(shadowIntensity);
        filterPostProcessor.addFilter(dropShadowFilter);

        getViewPort().addProcessor(filterPostProcessor);

        inputManager.addMapping("adaptShadowIntesity", new KeyTrigger(KeyInput.KEY_O));
        inputManager.addMapping("moveFloor", new KeyTrigger(KeyInput.KEY_L));
        inputManager.addListener(this, "adaptShadowIntesity", "moveFloor");

        stateManager.attach(new FlyCamAppState());
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if ("adaptShadowIntesity".equals(name) && !isPressed) {
            shadowIntensity += 0.05f;
            if (shadowIntensity > 2.0f + FastMath.ZERO_TOLERANCE) {
                shadowIntensity = 0;
            }
            LoggerFactory.getLogger(ShadowTest.class).info("Shadow intensity: {}", shadowIntensity);
            dropShadowFilter.setShadowIntensity(shadowIntensity);
        } else if ("moveFloor".equals(name) && !isPressed) {
            float height = floor.getLocalTranslation().y;
            height += 0.1f;
            if (height > -1f + FastMath.ZERO_TOLERANCE) {
                height = -3.5f;
            }
            LoggerFactory.getLogger(ShadowTest.class).info("Floor height: {}", height);
            floor.setLocalTranslation(floor.getLocalTranslation().setY(height));
        }
    }
}
