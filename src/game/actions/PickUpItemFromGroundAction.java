package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Picks up one item from the ground at the actor's current tile or an adjacent tile.
 * If a target location is provided, the actor moves there before picking up.
 */
public class PickUpItemFromGroundAction extends Action {
    private final Item item;
    private final Location targetLocation;
    private final String direction;

    public PickUpItemFromGroundAction(Item item) {
        this.item = item;
        this.targetLocation = null;
        this.direction = null;
    }

    public PickUpItemFromGroundAction(Location targetLocation, String direction, Item item) {
        this.item = item;
        this.targetLocation = targetLocation;
        this.direction = direction;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        Location pickupLocation;

        if (targetLocation != null) {
            map.moveActor(actor, targetLocation);
            pickupLocation = targetLocation;
        } else {
            pickupLocation = map.locationOf(actor);
        }

        if (pickupLocation.getItems().contains(item)) {
            actor.getInventory().add(item);
            pickupLocation.removeItem(item);

            if (targetLocation != null) {
                return actor + " moves " + direction + " and picks up " + item;
            }
            return actor + " picks up " + item;
        }

        return actor + " could not pick up " + item;
    }

    @Override
    public String menuDescription(Actor actor) {
        if (targetLocation != null) {
            return actor + " moves " + direction + " and picks up " + item;
        }
        return actor + " picks up " + item;
    }
}
