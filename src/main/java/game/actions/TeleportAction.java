package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.Teleportable;

/**
 * Teleport to a specific destination using a Teleportable device, then apply the
 * device's side effects.
 *
 * Destination choice is handled by exposing one TeleportAction per destination
 * in allowableActions() (rather than trying to prompt from within execute()).
 */
public class TeleportAction extends Action {
    private final Teleportable device;
    private final Location destination;

    /**
     * Constructor for TeleportAction.
     *
     * @param device the teleportable device being used
     * @param destination the chosen destination
     */
    public TeleportAction(Teleportable device, Location destination) {
        this.device = device;
        this.destination = destination;
    }

    /**
     * Execute teleportation to the chosen destination and apply side effects.
     * @param actor the actor performing the teleportation
     * @param map the current game map
     * @return a description of the teleportation result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Location source = map.locationOf(actor);
        GameMap destinationMap = destination.map();
        if (!destination.canActorEnter(actor)) {
            return "Teleportation failed: destination is blocked or occupied.";
        }
        destinationMap.moveActor(actor, destination);
        device.onTeleport(actor, source, destination, map);
        Location finalLocation = destinationMap.locationOf(actor);
        return actor + " teleported to " + finalLocation;
    }

    @Override
    public String menuDescription(Actor actor) {
        return "Teleport to " + destination;
    }
}
