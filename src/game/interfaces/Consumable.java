package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * Represents something that can be consumed by an actor.
 */
public interface Consumable {

    /**
     * Check whether this consumable can currently be consumed by the actor
     * through the normal worker flow (usually from inventory).
     *
     * @param actor the actor attempting to consume it
     * @return true if it can be consumed, false otherwise
     */
    boolean canConsume(Actor actor);

    /**
     * Apply the normal consumption effect to the actor.
     *
     * @param actor the actor consuming it
     * @return result description
     */
    String consume(Actor actor);

    /**
     * Description shown in the menu when the actor can consume it.
     *
     * @param actor the actor who may consume it
     * @return menu description
     */
    String menuDescription(Actor actor);

    /**
     * Check whether this consumable can be consumed directly from the ground.
     * By default, things are not ground-consumable unless a class explicitly allows it.
     *
     * @param actor the actor attempting to consume it from the ground
     * @return true if direct ground consumption is allowed
     */
    default boolean canConsumeFromGround(Actor actor) {
        return false;
    }

    /**
     * Apply the consumption effect when this consumable is eaten directly from the ground.
     *
     * @param actor the actor consuming it from the ground
     * @return result description
     */
    default String consumeFromGround(Actor actor) {
        return actor + " cannot consume " + this + " from the ground.";
    }

    /**
     * Check whether the consumable should be removed from the ground
     * after a direct ground consumption.
     *
     * @return true if it should disappear from the ground
     */
    default boolean shouldRemoveAfterGroundConsume() {
        return false; // let detail class determine by themselves
    }
}