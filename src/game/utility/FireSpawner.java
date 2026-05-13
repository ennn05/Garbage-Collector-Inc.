package game.utility;

import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Fire;
import game.interfaces.FireHazard;

/**
 * Utility class for spawning fire around a location.
 */
public class FireSpawner {

    /**
     * Spawn standard fire on all surrounding tiles of the given location.
     * The standard fire duration is 5 turns.
     *
     * @param location the centre location
     */
    public void spawnAround(Location location) {
        spawnAround(location, Fire.DEFAULT_FIRE_DURATION);
    }

    /**
     * Spawn fire with a custom duration on all surrounding tiles of the given location.
     *
     * @param location the centre location
     * @param duration the number of turns the fire should remain
     */
    public void spawnAround(Location location, int duration) {
        for (Location surroundingLocation : location.getNearbyLocations(1)) {
            FireHazard fireHazard = surroundingLocation.getGroundAs(FireHazard.class);

            if (fireHazard == null) {
                surroundingLocation.setGround(new Fire(surroundingLocation.getGround(), duration));
            }
        }
    }
}