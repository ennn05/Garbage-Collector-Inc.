package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.actions.DetonateAction;
import game.enums.ItemStatistics;

public class BeaconDetonator extends Item {
    private static final int DETONATE_RANGE = 5;
    private static final int WEIGHT = 1;

    public BeaconDetonator() {
        super("Remote Detonator", '[');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
    }

    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        return new ActionList(new DetonateAction(DETONATE_RANGE));
    }
}
