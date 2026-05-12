package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * Represents an object that can create a new actor instance on demand.
 */
public interface Spawnable {
    /**
     * Creates and returns a new actor instance.
     *
     * @return a newly spawned actor
     */
    Actor spawn();
}
