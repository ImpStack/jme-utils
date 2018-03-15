package org.impstack.jme.state;

import com.jme3.app.Application;
import com.jme3.scene.Node;

/**
 * Class that represent the lifecycle of a View.
 * The {@link #initialize()} and {@link #cleanup()} methods are called in {@link BaseGuiAppState}.
 */
public abstract class ViewNode<T extends BaseGuiAppState> extends Node {

    private T guiAppstate;

    protected abstract void initialize();

    protected abstract void cleanup();

    public T getGuiAppstate() {
        return guiAppstate;
    }

    public void setGuiAppstate(T guiAppstate) {
        this.guiAppstate = guiAppstate;
    }

    public Application getApplication() {
        return getGuiAppstate().getApplication();
    }

}
