package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import java.util.List;

/**
 * Interface for teleportable devices/items that allow actors to teleport between locations.
 * Implementations include TeleportationTube, AlienCube, and MagicCircle.
 */
public interface Teleportable {

    /**
     * Get the list of possible destination locations for teleportation.
     * @param map the current game map
     * @return a list of possible destination locations
     */
    List<Location> getDestinations(GameMap map);

    /**
     * Execute the teleportation and any side effects.
     * This is called after the actor has selected a destination.
     * @param actor the actor being teleported
     * @param source the source location
     * @param destination the destination location
     * @param map the current game map
     */
    void onTeleport(Actor actor, Location source, Location destination, GameMap map);
}
