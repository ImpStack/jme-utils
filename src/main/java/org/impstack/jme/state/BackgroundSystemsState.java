package org.impstack.jme.state;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.util.SafeArrayList;
import com.simsilica.sim.GameLoop;
import com.simsilica.sim.GameSystem;
import com.simsilica.sim.GameSystemManager;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Manages the life-cycle of a set of GameSystems.
 * The GameLoop manages the GameSystemManager, when the loop is started,
 * the systems are started. When the loop is stopped, the systems are stopped
 * and terminated.
 * This class also manages a scheduled single thread executor that can be used for intensive computing tasks
 * that would otherwise block the gameloop.
 */
public class BackgroundSystemsState extends BaseAppState {

    private GameSystemManager systemManager;
    private GameLoop gameLoop;
    private final SafeArrayList<GameSystem> systems = new SafeArrayList<>(GameSystem.class);

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

    /**
     *  Enqueues a task that will be run at the beginning of the next
     *  update() call on the update thread.  This delegates to the
     *  TaskDispatcher system registered to this GameSystemManager.
     *  @param callable the callable to enqueue
     *  @param <T> the type of the callable
     *  @return the future of the async computation
     */
    public <T> Future<T> enqueue(Callable<T> callable ) {
        return systemManager.enqueue(callable);
    }

    public <T extends GameSystem> boolean attach(T system) {
        synchronized (systems) {
            if (!systems.contains(system)) {
                systems.add(system);
                systemManager.addSystem(system);
                return true;
            }
        }
        return false;
    }

    public <T extends GameSystem> boolean detach(T system) {
        synchronized (systems) {
            if (systems.contains(system)) {
                systems.remove(system);
                systemManager.removeSystem(system);
                return true;
            }
        }
        return false;
    }

    public <T extends GameSystem> T getSystem(Class<T> system) {
        synchronized (systems) {
            GameSystem[] systemsArray = systems.getArray();
            for (GameSystem s : systemsArray) {
                if (system.isAssignableFrom(s.getClass())) {
                    return (T) s;
                }
            }
        }
        return null;
    }

}
