package org.impstack.jme.state;

import com.jme3.app.Application;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.shape.Sphere;
import org.impstack.jme.JmeLauncher;
import org.impstack.jme.scene.GeometryUtils;

/**
 * A test case for the abstract BaseFadeState. When the {@link MyFadeState} is attached, a fade in is triggered. When
 * the state is disabled, a fade out is triggered.
 */
public class BaseFadeTest extends JmeLauncher {

    public static void main(String[] args) {
        new BaseFadeTest().start();
    }

    @Override
    public void init() {
        MyFadeState myFadeState = new MyFadeState(ColorRGBA.Green, true);
        myFadeState.setDuration(1f);
        getStateManager().attach(myFadeState);
    }

    float counter;

    @Override
    public void simpleUpdate(float tpf) {
        counter += tpf;
        if (counter >= 3) getStateManager().getState(MyFadeState.class).setEnabled(false);
    }

    private class MyFadeState extends BaseFadeState {

        public MyFadeState(ColorRGBA overlayColor, boolean overlayVisible) {
            super(overlayColor, overlayVisible);
        }

        @Override
        protected void initialize(Application app) {
            System.out.println("Started");
            rootNode.attachChild(new GeometryUtils(getApplication()).createGeometry(new Sphere(32, 32, 1), ColorRGBA.Red));
        }

        @Override
        protected void cleanup(Application app) {
            System.out.println("Stopped");
        }

        @Override
        protected void onEnable() {
            System.out.println("I am fully faded in! :-)");
        }

        @Override
        protected void onDisable() {
            System.out.println("I faded out :-(");
        }
    }
}
