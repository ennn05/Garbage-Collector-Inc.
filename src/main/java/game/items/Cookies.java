package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.economy.Wallet;
import game.enums.Ability;
import game.enums.ItemStatistics;
import game.interfaces.Host;
import game.interfaces.Sellable;
import game.interfaces.Spawner;
import game.status.HiveStatus;
import game.status.ItemDegradeStatus;

/**
 * A pack of cookies that can be consumed five times.
 */
public class Cookies extends ConsumableItem implements Sellable, Host {
    private static final int WEIGHT = 2;
    private static final int TOTAL_COOKIES = 5;
    private static final int HEAL_AMOUNT = 1;
    private static final int MAX_HP_DECREASE = 1;
    private static final int HIVE_SPAWN_INTERVAL = 1;

    public Cookies() {
        super("Cookies", '◍');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.addNewStatistic(ItemStatistics.DURABILITY, new BaseStatistic(TOTAL_COOKIES));
    }

    /**
     * Get the remaining cookies.
     *
     * @return remaining cookies
     */
    public int getRemainingCookies() {
        return this.getStatistic(ItemStatistics.DURABILITY);
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
        return actor.getInventory().getItems().contains(this) && this.getRemainingCookies() > 0;
    }

    /**
     * Apply one cookie's effect and reduce the remaining amount by one.
     *
     * @param actor the actor eating one cookie
     * @return result description
     */
    private String applyCookieEffect(Actor actor) {
        this.modifyStatistic(ItemStatistics.DURABILITY, StatisticOperations.DECREASE, 1);

        String result;
        if (actor.hasAbility(Ability.STERILISING)) {
            actor.heal(HEAL_AMOUNT);
            result = actor + " eats a sterilised cookie and heals " + HEAL_AMOUNT + " health point.";
        } else {
            actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.DECREASE, MAX_HP_DECREASE);
            result = actor + " eats a cookie and permanently loses " + MAX_HP_DECREASE + " maximum health point.";
        }

        if (this.getRemainingCookies() > 0) {
            result += " Remaining cookies: " + this.getRemainingCookies() + ".";
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

        if (this.getRemainingCookies() == 0) {
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
        return this.getRemainingCookies() > 0;
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

        if (this.getRemainingCookies() == 0) {
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
        return this.getRemainingCookies() == 0;
    }

    /**
     * Get the selling price of the cookies.
     * Each remaining cookie is worth 1 credit.
     *
     * @return selling price in credits
     */
    @Override
    public int getSellPrice() {
        return this.getRemainingCookies();
    }

    /**
     * Apply the organic processing fee after selling cookies.
     *
     * @param seller the actor selling this item
     * @param map the map where the transaction happens
     * @param terminalLocation the location of the Supercomputer
     * @param wallet the seller's wallet
     * @return result description
     */
    @Override
    public String onSold(Actor seller, GameMap map, Location terminalLocation, Wallet wallet) {
        seller.hurt(this.getRemainingCookies());
        return seller + " pays an organic processing fee and loses "
                + this.getRemainingCookies() + " health point(s).";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " eats a Cookie";
    }

    /**
     * Infects this cookie pack and turns it into a living hive.
     *
     * @param otherActor the actor causing the infection
     * @param gameMap the map where the infection occurs
     * @return a description of the infection and hive transformation
     */
    @Override
    public String infect(Actor otherActor, GameMap gameMap) {
        Spawner spawner = otherActor.asCapability(Spawner.class).orElse(null);
        if (spawner == null) {
            return otherActor + " infects " + this;
        }
        this.addStatus(new ItemDegradeStatus());
        return otherActor + " infects " + this + "\n" + this.hive(spawner);
    }

    /**
     * Turns this cookie pack into a hive that can spawn the provided spawner.
     *
     * @param spawner the spawn source used by the hive
     * @return a description of the hive transformation
     */
    @Override
    public String hive(Spawner spawner) {
        this.addStatus(new HiveStatus(spawner, HIVE_SPAWN_INTERVAL));
        return this + " becomes a living hive.";
    }

    /**
     * Performs any hive side effects for this item.
     * <p>
     * Cookies do not have an additional hive side effect beyond their status changes.
     */
    @Override
    public void hiveEffect() {
    }
}