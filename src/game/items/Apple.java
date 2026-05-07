package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.enums.Ability;
import game.enums.ItemStatistics;
import game.status.PoisonStatus;

/**
 * A spoiled apple that is toxic unless the consumer has sterilising support.
 */
public class Apple extends ConsumableItem {
    private static final int WEIGHT = 1;
    private static final int HEAL_AMOUNT = 3;
    private static final int POISON_TURNS = 5;

    public Apple() {
        super("Apple", 'ó');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
    }

    /**
     * Apple can only be consumed normally when it is inside the actor's inventory.
     *
     * @param actor the actor attempting to consume it
     * @return true if the actor is carrying this apple
     */
    @Override
    public boolean canConsume(Actor actor) {
        return actor.getInventory().getItems().contains(this);
    }

    /**
     * Apply the actual apple effect without deciding where the apple came from.
     *
     * @param actor the actor eating the apple
     * @return result description
     */
    private String applyAppleEffect(Actor actor) {
        if (actor.hasAbility(Ability.STERILISING)) {
            actor.heal(HEAL_AMOUNT);
            return actor + " eats the Apple safely and heals " + HEAL_AMOUNT + " health points.";
        }

        actor.addStatus(new PoisonStatus(POISON_TURNS));
        return actor + " eats the Apple and is poisoned for " + POISON_TURNS + " turns.";
    }

    /**
     * Normal worker consumption from inventory.
     *
     * @param actor the actor consuming it
     * @return result description
     */
    @Override
    public String consume(Actor actor) {
        actor.getInventory().remove(this);
        return applyAppleEffect(actor);
    }

    /**
     * Apple may also be consumed directly from the ground by creatures such as Slime.
     *
     * @param actor the actor attempting to consume it from the ground
     * @return true because apple can be eaten from the ground
     */
    @Override
    public boolean canConsumeFromGround(Actor actor) {
        return true;
    }

    /**
     * Direct ground consumption.
     *
     * @param actor the actor consuming it from the ground
     * @return result description
     */
    @Override
    public String consumeFromGround(Actor actor) {
        return applyAppleEffect(actor);
    }

    /**
     * Apple is a one-time consumable, so it should disappear after ground consumption.
     *
     * @return true
     */
    @Override
    public boolean shouldRemoveAfterGroundConsume() {
        return true;
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " eats Apple";
    }
}