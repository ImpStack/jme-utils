package org.impstack.jme.state;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.renderer.RenderManager;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An AppState that is a full copy of {@link BaseAppState} that holds a {@link ViewNode} to represent a View. This
 * AppState's main purpose is to decouple the View part from the Controller and Model.
 *
 * The {@link ViewNode#initialize()} method is called right after the {@link BaseGuiAppState#initialize(Application)}
 * method. The {@link ViewNode#cleanup()} method is called right before the {@link BaseGuiAppState#cleanup()} method.
 */
public abstract class BaseGuiAppState<T extends ViewNode> implements AppState {

    static final Logger log = Logger.getLogger(BaseAppState.class.getName());

    private Application app;
    private boolean initialized;
    private boolean enabled = true;
    private final T viewNode;

    public BaseGuiAppState(T viewNode) {
        this.viewNode = viewNode;
        this.viewNode.setGuiAppstate(this);
    }

    /**
     *  Called during initialization once the app state is
     *  attached and before onEnable() is called.
     * @param app the application
     */
    protected abstract void initialize( Application app );

    /**
     *  Called after the app state is detached or during
     *  application shutdown if the state is still attached.
     *  onDisable() is called before this cleanup() method if
     *  the state is enabled at the time of cleanup.
     * @param app the application
     */
    protected abstract void cleanup( Application app );

    /**
     *  Called when the state is fully enabled, ie: is attached
     *  and isEnabled() is true or when the setEnabled() status
     *  changes after the state is attached.
     */
    protected abstract void onEnable();

    /**
     *  Called when the state was previously enabled but is
     *  now disabled either because setEnabled(false) was called
     *  or the state is being cleaned up.
     */
    protected abstract void onDisable();

    /**
     *  Do not call directly: Called by the state manager to initialize this
     *  state post-attachment.
     *  This implementation calls initialize(app) and then onEnable() if the
     *  state is enabled.
     */
    @Override
    public final void initialize(AppStateManager stateManager, Application app ) {
        log.log(Level.FINEST, "initialize():{0}", this);

        this.app = app;
        initialized = true;
        initialize(app);
        viewNode.initialize();
        if( isEnabled() ) {
            log.log(Level.FINEST, "onEnable():{0}", this);
            onEnable();
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

    public final <T extends AppState> T getState( Class<T> type ) {
        return getStateManager().getState(type);
    }

    public T getViewNode() {
        return viewNode;
    }

    @Override
    public final void setEnabled( boolean enabled ) {
        if( this.enabled == enabled )
            return;
        this.enabled = enabled;
        if( !isInitialized() )
            return;
        if( enabled ) {
            log.log(Level.FINEST, "onEnable():{0}", this);
            onEnable();
        } else {
            log.log(Level.FINEST, "onDisable():{0}", this);
            onDisable();
        }
    }

    @Override
    public final boolean isEnabled() {
        return enabled;
    }

    @Override
    public void stateAttached( AppStateManager stateManager ) {
    }

    @Override
    public void stateDetached( AppStateManager stateManager ) {
    }

    @Override
    public void update( float tpf ) {
    }

    @Override
    public void render( RenderManager rm ) {
    }

    @Override
    public void postRender() {
    }

    /**
     *  Do not call directly: Called by the state manager to terminate this
     *  state post-detachment or during state manager termination.
     *  This implementation calls onDisable() if the state is enabled and
     *  then cleanup(app).
     */
    @Override
    public final void cleanup() {
        log.log(Level.FINEST, "cleanup():{0}", this);

        if( isEnabled() ) {
            log.log(Level.FINEST, "onDisable():{0}", this);
            onDisable();
        }
        viewNode.cleanup();
        cleanup(app);
        initialized = false;
    }

}
