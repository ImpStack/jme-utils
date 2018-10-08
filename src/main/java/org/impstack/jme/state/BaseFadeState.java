package org.impstack.jme.state;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.simsilica.lemur.Panel;
import org.impstack.jme.ApplicationContext;
import org.impstack.jme.lemur.GuiHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A custom implementation of JME's {@link com.jme3.app.state.BaseAppState} with fade in/out support.
 * When the state is enabled, a fade in is triggered. When the state is disabled a fade out is triggered.
 * <p>
 * When the fade in animation is done, the {@link #onEnable()} method is called.
 * When the fade out animation is done, the {@link #onDisable()} method is called.
 */
public abstract class BaseFadeState implements AppState {

    static final Logger log = LoggerFactory.getLogger(BaseFadeState.class);

    private Application app;
    private boolean initialized;
    private boolean enabled = true;
    private Panel overlay;
    private ColorRGBA overlayColor = ColorRGBA.Black;
    private boolean overlayVisible = true;
    private boolean animationRunning = false;
    private float duration = 0.3f;
    private float value = 0f; // 0 = transparent; 1 = black
    private float direction = 1f; // 1 = fade out; -1 = fade in

    public BaseFadeState() {
    }

    public BaseFadeState(ColorRGBA overlayColor, boolean overlayVisible) {
        this.overlayColor = overlayColor;
        this.overlayVisible = overlayVisible;
    }

    /**
     * Called during initialization once the app state is
     * attached and before onEnable() is called.
     *
     * @param app the application
     */
    protected abstract void initialize(Application app);

    /**
     * Called after the app state is detached or during
     * application shutdown if the state is still attached.
     * onDisable() is called before this cleanup() method if
     * the state is enabled at the time of cleanup.
     *
     * @param app the application
     */
    protected abstract void cleanup(Application app);

    /**
     * Called when the state is fully enabled, ie: is attached
     * and isEnabled() is true or when the setEnabled() status
     * changes after the state is attached.
     */
    protected abstract void onEnable();

    /**
     * Called when the state was previously enabled but is
     * now disabled either because setEnabled(false) was called
     * or the state is being cleaned up.
     */
    protected abstract void onDisable();

    /**
     * Do not call directly: Called by the state manager to initialize this
     * state post-attachment.
     * This implementation calls initialize(app) and then onEnable() if the
     * state is enabled.
     */
    @Override
    public final void initialize(AppStateManager stateManager, Application app) {
        log.trace("initialize():{}", this);

        this.app = app;
        initialized = true;

        overlay = new Panel(GuiHelper.getWidth(), GuiHelper.getHeight(), overlayColor);
        overlay.setLocalTranslation(0, GuiHelper.getHeight(), 999);
        value = overlayVisible ? 1 : 0;
        overlay.setAlpha(value);
        ApplicationContext.INSTANCE.getGuiNode().attachChild(overlay);

        initialize(app);
        if (isEnabled()) {
            fadeIn();
        }
    }

    @Override
    public final boolean isInitialized() {
        return initialized;
    }

    public final Application getApplication() {
        return app;
    }

    public final AppStateManager getStateManager() {
        return app.getStateManager();
    }

    public final <T extends AppState> T getState(Class<T> type) {
        return getStateManager().getState(type);
    }

    @Override
    public final void setEnabled(boolean enabled) {
        if (this.enabled == enabled)
            return;
        if (!isInitialized())
            return;
        if (enabled) {
            fadeIn();
        } else {
            fadeOut();
        }
    }

    @Override
    public final boolean isEnabled() {
        return enabled;
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
    }

    @Override
    public final void update(float tpf) {
        if (animationRunning) {
            value += tpf * (direction / duration);

            if (direction == -1 && value < 0) {
                log.trace("Fade in completed");
                value = 0;
                animationRunning = false;
                overlayVisible = false;
                fadeInCompleted();
            }

            if (direction == 1 && value > 1) {
                log.trace("Fade out completed");
                value = 1;
                animationRunning = false;
                overlayVisible = true;
                fadeOutCompleted();
            }

            overlay.setAlpha(value);
        }

        running(tpf);
    }

    @Override
    public void render(RenderManager rm) {
    }

    @Override
    public void postRender() {
    }

    /**
     * Do not call directly: Called by the state manager to terminate this
     * state post-detachment or during state manager termination.
     * This implementation calls onDisable() if the state is enabled and
     * then cleanup(app).
     */
    @Override
    public final void cleanup() {
        log.trace("cleanup():{}", this);

        if (isEnabled()) {
            log.trace("onDisable():{}", this);
            onDisable();
        }

        overlay.removeFromParent();

        cleanup(app);
        initialized = false;
    }

    void fadeIn() {
        if (!animationRunning) {
            log.trace("Fading in...");
            value = 1;
            direction = -1;
            animationRunning = true;
        }
    }

    void fadeOut() {
        if (!animationRunning) {
            log.trace("Fading out...");
            value = 0;
            direction = 1;
            animationRunning = true;
        }
    }

    void running(float tpf) {
    }

    private void fadeInCompleted() {
        this.enabled = true;
        log.trace("onEnable():{}", this);
        onEnable();
    }

    private void fadeOutCompleted() {
        this.enabled = false;
        log.trace("onDisable():{}", this);
        onDisable();
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
    public void setOverlayVisible(boolean visible) {
        if (visible != overlayVisible) {
            overlayVisible = visible;
            value = overlayVisible ? 1 : 0;
            overlay.setAlpha(value);
        }
    }

    public boolean isOverlayVisible() {
        return overlayVisible;
    }

}
