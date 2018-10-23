package org.impstack.jme.state;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.simsilica.sim.GameLoop;
import com.simsilica.sim.GameSystemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the life-cycle of a set of GameSystems.
 * The GameLoop manages the GameSystemManager, when the loop is started,
 * the systems are started. When the loop is stopped, the systems are stopped
 * and terminated.
 * This class also manages a scheduled single thread executor that can be used for intensive computing tasks
 * that would otherwise block the gameloop.
 */
public class BackgroundSystemsState extends BaseAppState {

    private static final Logger LOG = LoggerFactory.getLogger(BackgroundSystemsState.class);

    private GameSystemManager systemManager;
    private GameLoop gameLoop;
    //private final Map<Class, Object> index = new ConcurrentHashMap<>();
    //private final SafeArrayList<GameSystem> systems = new SafeArrayList<>(GameSystem.class);

    @Override
    protected void initialize(Application app) {
        systemManager = new GameSystemManager();
        gameLoop = new GameLoop(systemManager);
    }

    @Override
    protected void onEnable() {
        gameLoop.start();
    }

    @Override
    protected void onDisable() {
        gameLoop.stop();
    }

    @Override
    protected void cleanup(Application app) {
    }

    public GameSystemManager getSystemManager() {
        return systemManager;
    }

}
