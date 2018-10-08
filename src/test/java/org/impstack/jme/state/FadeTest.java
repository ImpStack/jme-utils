package org.impstack.jme.state;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.shape.Sphere;
import org.impstack.jme.JmeLauncher;
import org.impstack.jme.scene.GeometryUtils;

/**
 * A test case for the {@link FadeState}
 * Press 'i' to trigger a fade in, press 'o' to trigger a fade out.
 */
public class FadeTest extends JmeLauncher implements ActionListener {

    public static void main(String[] args) {
        new FadeTest().start();
    }

    @Override
    public void init() {
        stateManager.attach(new FadeState(ColorRGBA.Red, true));

        rootNode.attachChild(new GeometryUtils(this).createGeometry(new Sphere(32, 32, 1), ColorRGBA.Blue));

        inputManager.addMapping("fadeIn", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("fadeOut", new KeyTrigger(KeyInput.KEY_O));
        inputManager.addListener(this, "fadeIn", "fadeOut");
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if ("fadeIn".equals(name) && !isPressed) {
            stateManager.getState(FadeState.class).fadeIn(FadeTest.class.getSimpleName());
        } else if ("fadeOut".equals(name) && !isPressed) {
            stateManager.getState(FadeState.class).fadeOut(FadeTest.class.getSimpleName());
        }
    }
}
