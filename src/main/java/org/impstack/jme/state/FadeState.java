package org.impstack.jme.state;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.ColorRGBA;
import com.simsilica.lemur.Panel;
import org.impstack.jme.ApplicationContext;
import org.impstack.jme.lemur.GuiHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * An application state that performs a fade-in (color to scene) and fade-out (scene to color).
 * The duration of the fade in/out animation can be set using {@link #setDuration(float)}.
 * <p>
 * Listeners ({@link FadeListener} can be registered and unregistered on the application state to receive events when
 * a fade-in or fade-out animation is completed.
 */
public class FadeState extends BaseAppState {

    private static final Logger LOG = LoggerFactory.getLogger(FadeState.class);

    private Panel overlay;
    private ColorRGBA overlayColor = ColorRGBA.Black;
    private boolean overlayVisible = true;
    private boolean animationRunning = false;
    private float duration = 0.3f;
    private float value = 0f; // 0 = transparent; 1 = black
    private float direction = 1f; // 1 = fade out; -1 = fade in
    private List<FadeListener> listeners = new ArrayList<>();

    public FadeState() {
    }

    public FadeState(ColorRGBA overlayColor, boolean overlayVisible) {
        this.overlayColor = overlayColor;
        this.overlayVisible = overlayVisible;
    }

    @Override
    protected void initialize(Application app) {
        overlay = new Panel(GuiHelper.getWidth(), GuiHelper.getHeight(), overlayColor);
        overlay.setLocalTranslation(0, GuiHelper.getHeight(), 999);
        value = overlayVisible ? 1 : 0;
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
                listeners.forEach(FadeListener::fadeInCompleted);
                LOG.trace("Fade in completed");
            }

            if (direction == 1 && value > 1) {
                value = 1;
                animationRunning = false;
                listeners.forEach(FadeListener::fadeOutCompleted);
                LOG.trace("Fade out completed");
            }

            overlay.setAlpha(value);
        }
    }

    public void fadeIn() {
        if (!animationRunning) {
            value = 1;
            direction = -1;
            animationRunning = true;
            LOG.trace("Fading in...");
        }
    }

    public void fadeOut() {
        if (!animationRunning) {
            value = 0;
            direction = 1;
            animationRunning = true;
            LOG.trace("Fading out...");
        }
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

    public void register(FadeListener listener) {
        listeners.add(listener);
    }

    public void unregister(FadeListener listener) {
        listeners.remove(listener);
    }
}
