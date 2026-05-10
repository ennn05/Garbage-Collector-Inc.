package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actions.UseFirstAidKitAction;
import game.economy.Wallet;
import game.enums.ItemStatistics;
import game.interfaces.Purchasable;
import game.interfaces.Sterilisable;

/**
 * A reusable first aid kit that permanently increases maximum health
 * and fully restores health when used.
 */
public class FirstAidKit extends Item implements Sterilisable, Purchasable {
    private static final int WEIGHT = 25;
    private static final int MAX_HEALTH_INCREASE = 1;
    private static final int COOLDOWN_TURNS = 20;
    private static final int PURCHASE_PRICE = 1000;

    private boolean sterilised = false;
    private int cooldownTurnsRemaining;

    public FirstAidKit() {
        super("First Aid Kit", '+');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.cooldownTurnsRemaining = 0;
    }

    /**
     * Check whether the first aid kit is ready to be used.
     *
     * @return true if the cooldown has finished, false otherwise
     */
    public boolean canUse() {
        return cooldownTurnsRemaining == 0;
    }

    /**
     * Get the number of turns remaining before the first aid kit can be used again.
     *
     * @return remaining cooldown turns
     */
    public int getCooldownTurnsRemaining() {
        return cooldownTurnsRemaining;
    }

    /**
     * Use the first aid kit on the given actor.
     * This permanently increases the actor's maximum health by 1
     * and fully restores the actor's health.
     *
     * @param actor the actor using the first aid kit
     */
    public void use(Actor actor) {
        if (canUse()) {
            actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.INCREASE, MAX_HEALTH_INCREASE);
            actor.heal(actor.getMaximumStatistic(ActorStatistics.HEALTH));
            cooldownTurnsRemaining = COOLDOWN_TURNS;
        }
    }

    /**
     * Reduce cooldown only while the item is being carried by an actor.
     *
     * @param currentLocation the location of the actor carrying this item
     * @param actor the actor carrying this item
     */
    @Override
    public void tick(Location currentLocation, Actor actor) {
        if (cooldownTurnsRemaining > 0) {
            cooldownTurnsRemaining--;
        }
    }

    /**
     * Provide the use action when the first aid kit is ready.
     *
     * @param owner the actor carrying the item
     * @param map the map where the actor is
     * @return allowable actions for this item while being carried
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();

        if (canUse()) {
            actions.add(new UseFirstAidKitAction(this));
        }

        return actions;
    }

    @Override
    public boolean isSterilised() {
        return sterilised;
    }

    @Override
    public void sterilise() {
        this.sterilised = true;
    }

    /**
     * Get the purchase price of the first aid kit.
     *
     * @return purchase price in credits
     */
    @Override
    public int getPurchasePrice() {
        return PURCHASE_PRICE;
    }

    /**
     * Create a new first aid kit after purchase.
     *
     * @return purchased first aid kit
     */
    @Override
    public Item createPurchasedItem() {
        return new FirstAidKit();
    }

    /**
     * Apply the successful purchase effect.
     * First Aid Kit has no extra effect when successfully purchased.
     *
     * @param buyer the actor buying this item
     * @param map the map where the transaction happens
     * @param terminalLocation the location of the Supercomputer
     * @param wallet the buyer's wallet
     * @return result description
     */
    @Override
    public String onPurchased(Actor buyer, GameMap map, Location terminalLocation, Wallet wallet) {
        return "";
    }

    /**
     * Apply the failed purchase effect.
     * Trying to buy a First Aid Kit without enough credits kills the buyer.
     *
     * @param buyer the actor attempting to buy this item
     * @param map the map where the transaction happens
     * @return result description
     */
    @Override
    public String onInsufficientCredits(Actor buyer, GameMap map) {
        buyer.hurt(buyer.getStatistic(ActorStatistics.HEALTH));

        return buyer + " attempts to buy a First Aid Kit without enough credits."
                + "\nThe Supercomputer is deeply upset and kills " + buyer + " on the spot."
                + "\n" + buyer.unconscious(map);
    }
}