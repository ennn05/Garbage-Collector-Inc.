package game.grounds;

import game.interfaces.Teleportable;
import game.actions.TeleportAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.actions.ActionList;
import game.utility.FireSpawner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A teleportation tube that allows actors to teleport to pre-set destinations.
 * Has a 50% malfunction rate that causes random teleportation within the destination map.
 * Creates fire around the final destination.
 */
public class TeleportationTube extends Ground implements Teleportable {
    private static final int FIRE_DURATION = 2;
    private static final double MALFUNCTION_CHANCE = 0.5;

    private final List<Location> destinations = new ArrayList<>();
    private final Random random = new Random();
    private final FireSpawner fireSpawner = new FireSpawner();

    /**
     * Constructor for TeleportationTube.
     */
    public TeleportationTube() {
        super('Φ', "Teleportation Tube");
    }

    /**
     * Add a destination to this tube's destination list.
     *
     * @param destination the destination location
     */
    public void addDestination(Location destination) {
        destinations.add(destination);
    }

    /**
     * Get the list of possible destinations.
     *
     * @param map the current game map
     * @return the list of pre-set destinations
     */
    @Override
    public List<Location> getDestinations(GameMap map) {
        return new ArrayList<>(destinations);
    }

    /**
     * Execute teleportation with 50% malfunction chance.
     * If the tube malfunctions, the actor is moved to a random valid location in the destination map.
     * After teleportation, fire is created around the final destination for 2 turns.
     *
     * @param actor the actor being teleported
     * @param source the source location
     * @param destination the intended destination location
     * @param map the current game map
     */
    @Override
    public void onTeleport(Actor actor, Location source, Location destination, GameMap map) {
        Location finalDestination = destination;

        if (random.nextDouble() < MALFUNCTION_CHANCE) {
            List<Location> randomLocations = getValidLocationsInMap(destination.map(), actor);

            if (!randomLocations.isEmpty()) {
                finalDestination = randomLocations.get(random.nextInt(randomLocations.size()));
            }
        }

        if (finalDestination != destination && finalDestination.canActorEnter(actor)) {
            finalDestination.map().moveActor(actor, finalDestination);
        }

        fireSpawner.spawnAround(finalDestination, FIRE_DURATION);
    }

    /**
     * Get all actions available for an actor at this location.
     *
     * @param actor the actor
     * @param location the location of this ground
     * @param direction the direction of this ground from the actor
     * @return action list containing TeleportAction
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();

        for (Location destination : getDestinations(location.map())) {
            if (destination != null && destination.canActorEnter(actor)) {
                actions.add(new TeleportAction(this, destination));
            }
        }

        return actions;
    }

    /**
     * Actors can enter the teleportation tube.
     *
     * @param actor the actor
     * @return true
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return true;
    }

    /**
     * Get all empty locations in a map that the actor can enter.
     * This avoids checking for a concrete Floor class.
     *
     * @param map the game map
     * @param actor the actor being teleported
     * @return list of valid destination locations
     */
    private List<Location> getValidLocationsInMap(GameMap map, Actor actor) {
        List<Location> validLocations = new ArrayList<>();

        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location location = map.at(x, y);

                if (location != null
                        && !location.containsAnActor()
                        && location.canActorEnter(actor)) {
                    validLocations.add(location);
                }
            }
        }

        return validLocations;
    }
}