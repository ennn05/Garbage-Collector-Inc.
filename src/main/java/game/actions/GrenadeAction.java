package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.enums.ItemStatistics;
import game.items.QuakeCharge;

public class GrenadeAction extends Action {
    private final QuakeCharge grenade;
    private final String direction;
    private final Location blastLocation;

    public GrenadeAction(QuakeCharge item, String direction, Location blastLocation) {
        this.grenade = item;
        this.direction = direction;
        this.blastLocation = blastLocation;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        grenade.triggerShockwave(blastLocation, map);
        grenade.modifyStatistic(ItemStatistics.DURABILITY, StatisticOperations.DECREASE, 1);
        if (grenade.getStatistic(ItemStatistics.DURABILITY) <= 0) {
            actor.getInventory().remove(grenade);
        }
        return actor + " threw a " + grenade + " and exploded at " + blastLocation;
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " throws a " + grenade + " at " + direction;
    }
}
