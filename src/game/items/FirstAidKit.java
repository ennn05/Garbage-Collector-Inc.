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
import game.enums.ItemStatistics;
import game.interfaces.Sterilisable;
/**
 * A reusable first aid kit that permanently increases maximum health
 * and fully restores health when used.
 */
public class FirstAidKit extends Item implements Sterilisable {
    private static final int WEIGHT = 25;
    private static final int MAX_HEALTH_INCREASE = 1;
    private static final int COOLDOWN_TURNS = 20;

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
}