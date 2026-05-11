package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.Teleportable;
import java.util.ArrayList;
import java.util.List;

/**
 * An action that handles teleportation for Teleportable devices.
 * When executed, presents the actor with a list of destination choices.
 */
public class TeleportAction extends Action {
    private Teleportable device;

    /**
     * Constructor for TeleportAction.
     * @param device the teleportable device being used
     */
    public TeleportAction(Teleportable device) {
        this.device = device;
    }

    /**
     * Execute the teleportation action.
     * This method presents the available destinations to the actor.
     * @param actor the actor performing the teleportation
     * @param map the current game map
     * @return a description of the teleportation result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Location currentLocation = map.locationOf(actor);
        List<Location> destinations = device.getDestinations(map);

        if (destinations.isEmpty()) {
            return "No valid teleportation destinations available.";
        }

        // If only one destination, teleport immediately
        if (destinations.size() == 1) {
            Location destination = destinations.get(0);
            teleportToDestination(actor, currentLocation, destination, map);
            return actor + " teleported to " + destination;
        }

        // If multiple destinations, this is handled via getAlternativeActions()
        // This shouldn't normally be reached
        return "Teleportation device active. Choose a destination.";
    }

    /**
     * Provide alternative actions to present destination choices as a menu.
     * @param actor the actor performing the action
     * @param map the current game map
     * @return a list of actions for each available destination
     */
    public List<Action> getAlternativeActions(Actor actor, GameMap map) {
        List<Action> actions = new ArrayList<>();
        Location currentLocation = map.locationOf(actor);
        List<Location> destinations = device.getDestinations(map);

        for (Location destination : destinations) {
            actions.add(new DestinationChoiceAction(device, currentLocation, destination));
        }

        return actions;
    }

    /**
     * Helper method to perform the actual teleportation.
     * @param actor the actor being teleported
     * @param source the source location
     * @param destination the destination location
     * @param map the current game map
     */
    private void teleportToDestination(Actor actor, Location source, Location destination, GameMap map) {
        // Move actor
        map.moveActor(actor, destination);
        // Call device's teleport effect
        device.onTeleport(actor, source, destination, map);
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " teleports";
    }

    /**
     * Inner action class for choosing a specific destination.
     */
    private class DestinationChoiceAction extends Action {
        private Teleportable device;
        private Location source;
        private Location destination;

        /**
         * Constructor for destination choice.
         * @param device the teleportable device
         * @param source the source location
         * @param destination the chosen destination
         */
        public DestinationChoiceAction(Teleportable device, Location source, Location destination) {
            this.device = device;
            this.source = source;
            this.destination = destination;
        }

        @Override
        public String execute(Actor actor, GameMap map) {
            map.moveActor(actor, destination);
            device.onTeleport(actor, source, destination, map);
            return actor + " teleported to " + destination;
        }

        @Override
        public String menuDescription(Actor actor) {
            return "Teleport to " + destination;
        }
    }
}
