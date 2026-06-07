package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.Consumable;

/**
 * An action for consuming one consumable item directly from the ground.
 */
public class ConsumeGroundItemAction extends Action {
    private final Item item;
    private final Consumable consumable;
    private final Location targetLocation; // consume target location
    private final String direction;

    public ConsumeGroundItemAction(Item item, Consumable consumable) {
        this.item = item;
        this.consumable = consumable;
        this.targetLocation = null;        // this location directly eat
        this.direction = null;
    }

    public ConsumeGroundItemAction(Location targetLocation, String direction, Item item, Consumable consumable) {
        this.item = item;
        this.consumable = consumable;
        this.targetLocation = targetLocation; // go to target_location then eat
        this.direction = direction;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        Location consumeLocation;

        if (targetLocation != null) {  //need to walk to the location first
            map.moveActor(actor, targetLocation);
            consumeLocation = targetLocation;
        } else {
            consumeLocation = map.locationOf(actor);
        }

        String result = consumable.consumeFromGround(actor);

        if (consumable.shouldRemoveAfterGroundConsume()) {
            consumeLocation.removeItem(item);
        }

        if (targetLocation != null) {
            return actor + " moves " + direction + " and " + result;
        }

        return result; // eat now
    }

    @Override
    public String menuDescription(Actor actor) {
        if (targetLocation != null) {
            return actor + " moves " + direction + " and consumes " + item + " from the ground";
        }
        return actor + " consumes " + item + " from the ground";
    }
}