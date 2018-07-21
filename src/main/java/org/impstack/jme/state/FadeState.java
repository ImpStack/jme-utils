package org.impstack.jme.state;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.ColorRGBA;
import com.simsilica.lemur.Panel;
import org.impstack.jme.ApplicationContext;
import org.impstack.jme.lemur.GuiHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An application state that performs a fade in (color to scene) and fade out (scene to color).
 * The duration of the fade in/out animation can be set using {@link #setDuration(float)}.
 */
public class FadeState extends BaseAppState {

    private static final Logger LOG = LoggerFactory.getLogger(FadeState.class);

    private Panel overlay;
    private ColorRGBA overlayColor = ColorRGBA.Black;
    private boolean attachOverlay = true;
    private boolean animationRunning = false;
    private float duration = 0.3f;
    private float value = 0f; // 0 = transparent; 1 = black
    private float direction = 1f; // 1 = fade out; -1 = fade in

    public FadeState() {
    }

    public FadeState(ColorRGBA overlayColor, boolean attachOverlay) {
        this.overlayColor = overlayColor;
        this.attachOverlay = attachOverlay;
    }

    @Override
    protected void initialize(Application app) {
        overlay = new Panel(GuiHelper.getWidth(), GuiHelper.getHeight(), overlayColor);
        overlay.setLocalTranslation(0, GuiHelper.getHeight(), 999);
        value = attachOverlay ? 1 : 0;
        overlay.setAlpha(value);
        ApplicationContext.INSTANCE.getGuiNode().attachChild(overlay);
    }

    @Override
    protected void cleanup(Application app) {
        overlay.removeFromParent();
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

    @Override
    public void update(float tpf) {
        if (animationRunning) {
            value += tpf * (direction / duration);

            if (direction == -1 && value < 0) {
                value = 0;
                animationRunning = false;
                LOG.trace("Fade in completed");
            }

            if (direction == 1 && value > 1) {
                value = 1;
                animationRunning = false;
                LOG.trace("Fade out completed");
            }

            overlay.setAlpha(value);
        }
    }

    public void fadeIn() {
        value = 1;
        direction = -1;
        animationRunning = true;
        LOG.trace("Fading in...");
    }

    public void fadeOut() {
        value = 0;
        direction = 1;
        animationRunning = true;
        LOG.trace("Fading out...");
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public boolean isAnimationRunning() {
        return animationRunning;
    }
}
