package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents an object that can create a new actor instance on demand.
 */
public interface Spawner {
    /**
     * Creates and returns a new actor instance.
     *
     * @param location the location of the spawner
     * @return a newly spawned actor
     */
    Actor spawn(Location location);
}
