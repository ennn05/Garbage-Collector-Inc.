package game.interfaces;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents an entity that can be spawned into the map.
 */
public interface Spawnable {
    /**
     * Executes the side effects of spawning this entity.
     *
     * @param location the location of the entity that is spawned on
     */
    void spawnEffect(Location location);
}
