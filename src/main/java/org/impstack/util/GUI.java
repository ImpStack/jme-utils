package org.impstack.util;

/**
 * An interface that handles the lifecycle of a GUI.
 */
public interface GUI {

    /**
     * Called to attach the {@link GUI}
     */
    public void attach();

    /**
     * Called to update the {@link GUI}.
     * Call this method every render pass when the {@link GUI} is attached.
     */
    public void update();

    /**
     * Called to detach the {@link GUI}
     */
    public void detach();

}
