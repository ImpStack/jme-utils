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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An application state that performs a fade-in (color to scene) and fade-out (scene to color).
 * The duration of the fade in/out animation can be set using {@link #setDuration(float)}.
 * <p>
 * Listeners ({@link FadeListener} can be registered and unregistered on the application state to receive events when
 * a fade-in or fade-out animation is completed.
 */
@Deprecated
public class FadeState extends BaseAppState {

    private static final Logger LOG = LoggerFactory.getLogger(FadeState.class);

    private Panel overlay;
    private ColorRGBA overlayColor = ColorRGBA.Black;
    private boolean overlayVisible = true;
    private boolean animationRunning = false;
    private float duration = 0.3f;
    private float value = 0f; // 0 = transparent; 1 = black
    private float direction = 1f; // 1 = fade out; -1 = fade in
    private String mapping; // the mapping name that triggered the fade-in/fade-out
    private Map<String, List<FadeListener>> listeners = new HashMap<>();

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
                LOG.trace("Fade in completed [{}]", mapping);
                value = 0;
                animationRunning = false;
                getListenersForMapping(mapping).forEach(FadeListener::fadeInCompleted);
            }

            if (direction == 1 && value > 1) {
                LOG.trace("Fade out completed [{}]", mapping);
                value = 1;
                animationRunning = false;
                getListenersForMapping(mapping).forEach(FadeListener::fadeOutCompleted);
            }

            overlay.setAlpha(value);
        }
    }

    public void fadeIn(String mapping) {
        if (!animationRunning) {
            LOG.trace("Fading in... [{}]", mapping);
            value = 1;
            direction = -1;
            animationRunning = true;
            this.mapping = mapping;
        }
    }

    public void fadeOut(String mapping) {
        if (!animationRunning) {
            LOG.trace("Fading out... [{}]", mapping);
            value = 0;
            direction = 1;
            animationRunning = true;
            this.mapping = mapping;
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

    public void register(String mapping, FadeListener listener) {
        getListenersForMapping(mapping).add(listener);
    }

    public void unregister(String mapping, FadeListener listener) {
        getListenersForMapping(mapping).remove(listener);
    }

    private List<FadeListener> getListenersForMapping(String mapping) {
        return listeners.computeIfAbsent(mapping, f -> new ArrayList<>());
    }
}
