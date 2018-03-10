package org.impstack.jme.state;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

/**
 * An AppState that can have child AppStates. When the parent AppState is attached, all the child AppStates will
 * also be attached. Same thing happens when the parent AppState is detached, all the child AppStates will be
 * detached. When the parent AppState is paused, all the child AppStates will also be paused. When the parent
 * AppState is enabled, all child AppStates will be enabled. There is no check for the previous state of the child!
 * <p>
 * Initialize is called after the stateAttached method is called. The stateDetached method is called before the cleanup
 * method. The child states are attached and enabled in a FIFO order, the disabling and detaching is in a LIFO order.
 * eg. A composite appstate with child states A, B and C are attached and enabled in this order: A, B, C. The childstates
 * are disabled and detached in this order: C, B, A.
 *
 * @author remy
 * @since 11/10/17.
 */
public class CompositeAppState extends BaseAppState {

    private static final Logger LOG = LoggerFactory.getLogger(CompositeAppState.class);

    List<AppState> childStates = new ArrayList<>();
    private boolean attached = false;

    public CompositeAppState(AppState... appStates) {
        this.childStates.addAll(Arrays.asList(appStates));
    }

    public void addChild(AppState appState) {
        childStates.add(appState);
        if (attached) {
            getStateManager().attach(appState);
        }
    }

    public void addChildren(AppState... appStates) {
        Arrays.stream(appStates).forEach(this::addChild);
    }

    public void removeChild(AppState appState) {
        childStates.remove(appState);
        if (attached) {
            getStateManager().detach(appState);
        }
    }

    public void removeChildren(AppState... appStates) {
        Arrays.stream(appStates).forEach(this::removeChild);
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {
        childStates.forEach(appState -> {
            LOG.debug("{} - attaching {}", childStates.indexOf(appState), appState);
            stateManager.attach(appState);
        });
        attached = true;
    }

    @Override
    protected void initialize(Application app) {
    }

    @Override
    protected void onEnable() {
        childStates.forEach(appState -> {
            LOG.debug("{} - enabling {}", childStates.indexOf(appState), appState);
            appState.setEnabled(true);
        });
    }

    @Override
    protected void onDisable() {
        ListIterator<AppState> iterator = childStates.listIterator(childStates.size());
        while (iterator.hasPrevious()) {
            AppState appState = iterator.previous();
            LOG.debug("{} - disabling {}", childStates.indexOf(appState), appState);
            appState.setEnabled(false);
        }
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
        ListIterator<AppState> iterator = childStates.listIterator(childStates.size());
        while (iterator.hasPrevious()) {
            AppState appState = iterator.previous();
            LOG.debug("{} - detaching {}", childStates.indexOf(appState), appState);
            stateManager.detach(appState);
        }
        attached = false;
    }

    @Override
    protected void cleanup(Application app) {
    }

}
