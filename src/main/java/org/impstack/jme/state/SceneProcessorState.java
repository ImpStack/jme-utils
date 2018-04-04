package org.impstack.jme.state;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.post.FilterPostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A state that maintains a FilterPostProcessor state on the main viewport ({@link Application#getViewPort()}
 * Only one {@link FilterPostProcessor} should be set on a {@link com.jme3.renderer.ViewPort}.
 */
public class SceneProcessorState extends BaseAppState {

    private static final Logger LOG = LoggerFactory.getLogger(SceneProcessorState.class);

    private FilterPostProcessor fpp;

    @Override
    protected void initialize(Application app) {
        fpp = new FilterPostProcessor(app.getAssetManager());
        /**
         * See:
         * https://hub.jmonkeyengine.org/t/java-lang-unsupportedoperationexception-framebuffer-already-initialized-when-setting-numsamples-on-filterpostprocessor/40305
         */
        //fpp.setNumSamples(app.getContext().getSettings().getSamples());

        app.getViewPort().addProcessor(fpp);
        LOG.debug("Adding {} with {} samples on {}", fpp, fpp.getNumSamples(), app.getViewPort());
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
        LOG.debug("Removing {} from {}", fpp, app.getViewPort());
    }

    public FilterPostProcessor getFpp() {
        return fpp;
    }
}
