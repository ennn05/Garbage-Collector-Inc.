package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.economy.Wallet;
import game.enums.Ability;
import game.enums.ItemStatistics;
import game.interfaces.Sellable;
import game.status.PoisonStatus;

/**
 * A spoiled apple that is toxic unless the consumer has sterilising support.
 */
public class Apple extends ConsumableItem implements Sellable {
    private static final int WEIGHT = 1;
    private static final int HEAL_AMOUNT = 3;
    private static final int CONSUME_POISON_TURNS = 5;

    private static final int SELL_PRICE = 1;
    private static final int SALE_POISON_TURNS = 2;
    private static final int SALE_POISON_DAMAGE = 2;

    public Apple() {
        super("Apple", 'ó');
        this.makePortable();
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

        actor.addStatus(new PoisonStatus(CONSUME_POISON_TURNS));
        return actor + " eats the Apple and is poisoned for " + CONSUME_POISON_TURNS + " turns.";
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

    /**
     * Get the selling price of the apple.
     *
     * @return selling price in credits
     */
    @Override
    public int getSellPrice() {
        return SELL_PRICE;
    }

    /**
     * Apply the selling effect of the apple.
     * Selling it poisons the seller unless they are carrying a Sterilisation Box.
     *
     * @param seller the actor selling this item
     * @param map the map where the transaction happens
     * @param terminalLocation the location of the Supercomputer
     * @param wallet the seller's wallet
     * @return result description
     */
    @Override
    public String onSold(Actor seller, GameMap map, Location terminalLocation, Wallet wallet) {
        if (seller.hasAbility(Ability.STERILISING)) {
            return seller + " is protected by a Sterilisation Box and avoids the apple poison.";
        }

        seller.addStatus(new PoisonStatus(SALE_POISON_TURNS, SALE_POISON_DAMAGE));
        return seller + " is poisoned for " + SALE_POISON_TURNS
                + " turns, taking " + SALE_POISON_DAMAGE + " damage per turn.";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " eats Apple";
    }
}