package game.utility;

import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Fire;

/**
 * Utility class for spawning fire around a location.
 */
public class FireSpawner {

    /**
     * Spawn fire on all surrounding tiles of the given location.
     *
     * @param location the centre location
     */
    public void spawnAround(Location location) {
        for (Location surroundingLocation : location.getNearbyLocations(1)) {
            if (!(surroundingLocation.getGround() instanceof Fire)) {
                surroundingLocation.setGround(new Fire(surroundingLocation.getGround()));
            }
        }
    }
}