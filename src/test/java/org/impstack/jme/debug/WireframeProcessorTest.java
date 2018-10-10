package org.impstack.jme.debug;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import org.impstack.jme.JmeLauncher;
import org.impstack.jme.scene.GeometryUtils;

/**
 * A test case for the {@link WireframeProcessor}.
 * When you press 'P' a wireframe is displayed on the entire scene with a random color.
 */
public class WireframeProcessorTest extends JmeLauncher implements ActionListener {

    private static final String MAPPING = "toggleWireframe";
    private static final Trigger TRIGGER = new KeyTrigger(KeyInput.KEY_P);

    private WireframeProcessor wireframeProcessor;

    public static void main(String[] args) {
        new WireframeProcessorTest().start();
    }

    @Override
    public void init() {
        GeometryUtils geometryUtils = new GeometryUtils(this);

        Geometry box = geometryUtils.createGeometry(new Box(0.5f, 0.5f, 0.5f), ColorRGBA.Green);
        Geometry sphere = geometryUtils.createGeometry(new Sphere(32, 32, 0.5f), ColorRGBA.Red);

        box.move(-2f, 0, 0);
        sphere.move(2f, 0, 0);

        rootNode.attachChild(box);
        rootNode.attachChild(sphere);

        wireframeProcessor = new WireframeProcessor(assetManager);

        inputManager.addMapping(MAPPING, TRIGGER);
        inputManager.addListener(this, MAPPING);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals(MAPPING) && !isPressed) {
            if (viewPort.getProcessors().contains(wireframeProcessor)) {
                viewPort.removeProcessor(wireframeProcessor);
                System.out.println("Removing " + wireframeProcessor);
            } else {
                // set a custom color
                wireframeProcessor.setColor(ColorRGBA.randomColor());
                // set a custom line thickness
                wireframeProcessor.setLineWidth(FastMath.nextRandomInt(1, 5));
                viewPort.addProcessor(wireframeProcessor);
                System.out.println("Adding " + wireframeProcessor);
            }
        }
    }
}
