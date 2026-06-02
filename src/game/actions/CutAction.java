package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Floor;
import game.interfaces.Cuttable;
import game.interfaces.CuttingTool;

import java.util.ArrayList;

/**
 * An action that allows an actor with a plasma cutter to cut cuttable objects.
 */
public class CutAction extends Action {
    private final Cuttable target;
    private final Location targetLocation;

    /**
     * Constructor.
     *
     * @param target the cuttable object to cut
     * @param targetLocation the location of the target object
     */
    public CutAction(Cuttable target, Location targetLocation) {
        this.target = target;
        this.targetLocation = targetLocation;
    }

    /**
     * Execute the cut action.
     *
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return result description
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        // Check if actor has a cutting tool
        boolean hasCuttingTool = !actor.getInventory().getItemsAs(CuttingTool.class).isEmpty();

        if (!hasCuttingTool) {
            return actor + " does not have a Plasma Cutter to cut with!";
        }

        // Check if target can be cut by this actor
        if (!target.canBeCut(actor)) {
            return actor + " cannot cut this object.";
        }

        // Perform the cut
        String result = target.onCut(actor, targetLocation);

        // Drop the item at the location
        Item droppedItem = target.getDroppedItem();
        if (droppedItem != null) {
            targetLocation.addItem(droppedItem);
        }

        // Transform ground to Floor if target is a cuttable ground; otherwise remove the item from inventory
        if (targetLocation.getGroundAs(Cuttable.class) != null) {
            targetLocation.setGround(new Floor());
        } else {
            for (Item item : new ArrayList<>(actor.getInventory().getItems())) {
                if (item.asCapability(Cuttable.class).orElse(null) == target) {
                    actor.getInventory().remove(item);
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Menu description.
     *
     * @param actor The actor performing the action.
     * @return menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " cuts with Plasma Cutter";
    }
}
