package game.items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.enums.ItemStatistics;

/**
 * A massive and heavy piece of obsolete display hardware.
 */
public class CRTMonitor extends Item {
    private static final int WEIGHT = 30;

    public CRTMonitor() {
        super("CRT Monitor", '◙');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
    }
}