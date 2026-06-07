package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.Depositable;

/**
 * Moves an actor to a target location and snatches one depositable resource
 * from that location.
 */
public class MoveSnatchResourceAction extends Action {
    private final Location targetLocation;
    private final String direction;
    private final Item resource;

    /**
     * Constructor.
     *
     * @param targetLocation the location to move to before snatching
     * @param direction the direction of the target location
     * @param resource the depositable resource to snatch
     */
    public MoveSnatchResourceAction(Location targetLocation, String direction, Item resource) {
        this.targetLocation = targetLocation;
        this.direction = direction;
        this.resource = resource;
    }

    /**
     * Moves the actor to the target tile and picks up the selected depositable resource.
     *
     * @param actor the actor performing the action
     * @param map the map where the action is performed
     * @return a message describing the result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (!targetLocation.canActorEnter(actor)) {
            return actor + " cannot move " + direction + " to snatch " + resource + ".";
        }

        map.moveActor(actor, targetLocation);

        Depositable depositable = resource.asCapability(Depositable.class).orElse(null);
        if (depositable == null || !targetLocation.getItems().contains(resource)) {
            return actor + " moves " + direction + " but finds no depositable resource to snatch.";
        }

        if (!actor.getInventory().add(resource)) {
            return actor + " moves " + direction + " but cannot carry " + resource + ".";
        }

        targetLocation.removeItem(resource);
        return actor + " moves " + direction + " and snatches " + resource + ".";
    }

    /**
     * Describes this action in menu form.
     *
     * @param actor the actor performing the action
     * @return the menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " moves " + direction + " and snatches " + resource;
    }
}