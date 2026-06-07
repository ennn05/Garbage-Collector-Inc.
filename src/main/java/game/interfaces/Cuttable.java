package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Interface for objects that can be cut with a plasma cutter.
 */
public interface Cuttable {
    /**
     * Check if an actor can cut this object.
     *
     * @param actor the actor attempting to cut
     * @return true if the actor can cut this object
     */
    boolean canBeCut(Actor actor);

    /**
     * Handle the cutting action and return the result message.
     *
     * @param actor the actor performing the cut
     * @param location the location of this object
     * @return result description of the cutting
     */
    String onCut(Actor actor, Location location);

    /**
     * Get the item that is dropped when this object is cut.
     *
     * @return the dropped item, or null if nothing is dropped
     */
    Item getDroppedItem();
}
