package org.impstack.jme.state;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.post.FilterPostProcessor;

/**
 * A state that maintains a FilterPostProcessor state on the main viewport ({@link Application#getViewPort()}
 */
public class SceneProcessorState extends BaseAppState {

    private FilterPostProcessor fpp;

    @Override
    protected void initialize(Application app) {
        fpp = new FilterPostProcessor(app.getAssetManager());

        app.getViewPort().addProcessor(fpp);
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

    @Override
    protected void cleanup(Application app) {
        app.getViewPort().removeProcessor(fpp);
    }

    public FilterPostProcessor getFpp() {
        return fpp;
    }
}
