package org.impstack.jme;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.simsilica.lemur.GuiGlobals;
import org.impstack.jme.config.ApplicationSettingsFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;

public abstract class JmeLauncher extends SimpleApplication {

    public JmeLauncher() {
        this((AppState) null);
    }

    public JmeLauncher(Map<String, String> overrides) {
        this(overrides, (AppState) null);
    }

    public JmeLauncher(AppState... initialStates) {
        this(null, initialStates);
    }

    public JmeLauncher(Map<String, String> overrides, AppState... initialStates) {
        super(initialStates);

        LogManager.getLogManager().getLogger("").setLevel(Level.ALL);
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        setSettings(ApplicationSettingsFactory.getAppSettings(overrides));
        setPauseOnLostFocus(false);
        setShowSettings(false);
    }

    @Override
    public void simpleInitApp() {
        // remove the default escape mapping
        getInputManager().deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);
        // initialize lemur globals, so that the default components can find what they need.
        GuiGlobals.initialize(this);
        // setup application context
        ApplicationContext.INSTANCE.init(this);
        // call the init method
        init();
    }

    public abstract void init();

}
