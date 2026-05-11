package game.grounds;

import game.interfaces.Teleportable;
import game.actions.TeleportAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A teleportation tube that allows actors to teleport to pre-set destinations.
 * Has a 50% malfunction rate that causes random teleportation within the destination map.
 * Sets adjacent tiles on fire at the destination.
 */
public class TeleportationTube extends Ground implements Teleportable {
    private List<Location> destinations = new ArrayList<>();
    private Random random = new Random();

    /**
     * Constructor for TeleportationTube.
     */
    public TeleportationTube() {
        super('Φ', "Teleportation Tube");
    }

    /**
     * Add a destination to this tube's destination list.
     * @param destination the destination location
     */
    public void addDestination(Location destination) {
        destinations.add(destination);
    }

    /**
     * Get the list of possible destinations.
     * @param map the current game map
     * @return the list of pre-set destinations
     */
    @Override
    public List<Location> getDestinations(GameMap map) {
        return new ArrayList<>(destinations);
    }

    /**
     * Execute teleportation with 50% malfunction chance.
     * Sets adjacent tiles on fire at destination and applies burn status to actor.
     * @param actor the actor being teleported
     * @param source the source location
     * @param destination the destination location (may be overridden by malfunction)
     * @param map the current game map
     */
    @Override
    public void onTeleport(Actor actor, Location source, Location destination, GameMap map) {
        Location finalDestination = destination;

        // 50% chance of malfunction
        if (random.nextDouble() < 0.5) {
            // Random destination within the destination map
            List<Location> randomLocs = getAllLocationInMap(map);
            if (!randomLocs.isEmpty()) {
                finalDestination = randomLocs.get(random.nextInt(randomLocs.size()));
            }
        }

        // Set adjacent tiles on fire
        setAdjacentTilesOnFire(finalDestination);
    }

    /**
     * Get all actions available for an actor at this location.
     * @param actor the actor
     * @param location the location of this ground
     * @param direction the direction of this ground from the actor
     * @return action list containing TeleportAction
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        actions.add(new TeleportAction(this));
        return actions;
    }

    /**
     * Actors can enter the teleportation tube.
     * @param actor the actor
     * @return true
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return true;
    }

    /**
     * Set all adjacent floor tiles on fire.
     * @param location the location whose adjacent tiles to set on fire
     */
    private void setAdjacentTilesOnFire(Location location) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            if (adjacent.getGround() instanceof Floor) {
                adjacent.setGround(new Fire(new Floor()));
            }
        }
    }

    /**
     * Get all locations in a map for random destination selection.
     * @param map the game map
     * @return list of all traversable locations in the map
     */
    private List<Location> getAllLocationInMap(GameMap map) {
        List<Location> allLocations = new ArrayList<>();
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);
                if (loc != null && loc.getGround() instanceof Floor) {
                    allLocations.add(loc);
                }
            }
        }
        return allLocations;
    }
}
