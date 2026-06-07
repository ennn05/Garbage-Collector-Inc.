package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * Represents an item that can be drunk by an actor.
 */
public interface Drinkable {

    /**
     * Check whether this drinkable item still has remaining uses.
     *
     * @return true if it can still be drunk, false otherwise
     */
    boolean canDrink();

    /**
     * Drink from this item and apply its effect to the actor.
     *
     * @param actor the actor drinking from this item
     * @return result description
     */
    String drink(Actor actor);
}