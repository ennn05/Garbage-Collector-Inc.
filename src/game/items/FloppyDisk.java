package game.items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.enums.ItemStatistics;

/**
 * A lightweight piece of ancient storage technology.
 */
public class FloppyDisk extends Item {
    private static final int WEIGHT = 1;

    public FloppyDisk() {
        super("Floppy Disk", '⊟');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
    }
}