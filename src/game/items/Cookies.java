package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.enums.Ability;
import game.enums.ItemStatistics;

/**
 * A pack of cookies that can be consumed five times.
 */
public class Cookies extends ConsumableItem {
    private static final int WEIGHT = 2;
    private static final int TOTAL_COOKIES = 5;
    private static final int HEAL_AMOUNT = 1;
    private static final int MAX_HP_DECREASE = 1;

    private int remainingCookies;

    public Cookies() {
        super("Cookies", '◍');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.remainingCookies = TOTAL_COOKIES;
    }

    /**
     * Cookies can only be consumed normally when they are inside the actor's inventory
     * and at least one cookie remains.
     *
     * @param actor the actor attempting to consume it
     * @return true if the actor is carrying this item and it still has cookies left
     */
    @Override
    public boolean canConsume(Actor actor) {
        return actor.getInventory().getItems().contains(this) && remainingCookies > 0;
    }

    /**
     * Apply one cookie's effect and reduce the remaining amount by one.
     *
     * @param actor the actor eating one cookie
     * @return result description
     */
    private String applyCookieEffect(Actor actor) {
        remainingCookies--;

        String result;
        if (actor.hasAbility(Ability.STERILISING)) {
            actor.heal(HEAL_AMOUNT);
            result = actor + " eats a sterilised cookie and heals " + HEAL_AMOUNT + " health point.";
        } else {
            actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.DECREASE, MAX_HP_DECREASE);
            result = actor + " eats a cookie and permanently loses " + MAX_HP_DECREASE + " maximum health point.";
        }

        if (remainingCookies > 0) {
            result += " Remaining cookies: " + remainingCookies + ".";
        }

        return result;
    }

    /**
     * Normal worker consumption from inventory.
     *
     * @param actor the actor consuming it
     * @return result description
     */
    @Override
    public String consume(Actor actor) {
        String result = applyCookieEffect(actor);

        if (remainingCookies == 0) {
            actor.getInventory().remove(this);
            result += " The Cookies are now fully consumed and removed from inventory.";
        }

        return result;
    }

    /**
     * Cookies may also be consumed directly from the ground by creatures such as Slime.
     *
     * @param actor the actor attempting to consume it from the ground
     * @return true if there is still at least one cookie left
     */
    @Override
    public boolean canConsumeFromGround(Actor actor) {
        return remainingCookies > 0;
    }

    /**
     * Direct ground consumption.
     *
     * @param actor the actor consuming it from the ground
     * @return result description
     */
    @Override
    public String consumeFromGround(Actor actor) {
        String result = applyCookieEffect(actor);

        if (remainingCookies == 0) {
            result += " The Cookies are now fully consumed.";
        }

        return result;
    }

    /**
     * Remove the cookie pack from the ground only when all cookies are gone.
     *
     * @return true if there are no cookies left
     */
    @Override
    public boolean shouldRemoveAfterGroundConsume() {
        return remainingCookies == 0;
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " eats a Cookie";
    }
}