package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.enums.ItemStatistics;
import game.items.QuakeCharge;

/**
 * Action that throws a {@link QuakeCharge} in a chosen direction, triggering its shockwave
 * at the computed blast location.
 * <p>
 * Each execution decreases the grenade's durability by 1. When durability reaches zero the
 * item is automatically removed from the actor's inventory.
 */
public class GrenadeAction extends Action {
    private final QuakeCharge grenade;
    private final String direction;
    private final Location blastLocation;

    /**
     * Creates a GrenadeAction for the given charge, direction label, and pre-computed blast tile.
     *
     * @param item          the QuakeCharge being thrown
     * @param direction     a human-readable direction label (e.g. "north")
     * @param blastLocation the tile where the grenade will detonate
     */
    public GrenadeAction(QuakeCharge item, String direction, Location blastLocation) {
        this.grenade = item;
        this.direction = direction;
        this.blastLocation = blastLocation;
    }

    /**
     * Triggers the grenade's shockwave at the blast location, then decrements durability.
     * Removes the grenade from the actor's inventory if durability is exhausted.
     *
     * @param actor the actor throwing the grenade
     * @param map   the current game map
     * @return a message describing the throw and detonation
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        grenade.triggerShockwave(blastLocation, map);
        grenade.modifyStatistic(ItemStatistics.DURABILITY, StatisticOperations.DECREASE, 1);
        if (grenade.getStatistic(ItemStatistics.DURABILITY) <= 0) {
            actor.getInventory().remove(grenade);
        }
        return actor + " threw a " + grenade + " and exploded at " + blastLocation;
    }

    /**
     * {@inheritDoc}
     *
     * @param actor the actor considering this action
     * @return a description of the throw direction for the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " throws a " + grenade + " at " + direction;
    }
}
