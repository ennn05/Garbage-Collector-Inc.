package game.status;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.enums.ItemStatistics;

/**
 * A status effect that decreases an item's durability each tick until it is fully degraded.
 * <p>
 * Once the durability reaches zero or below, this status becomes inactive.
 */
public class ItemDegradeStatus implements Status {
    private static final int DEGRADE_VALUE = 1;

    private boolean degraded = false;

    /**
     * Applies durability degradation to the current entity.
     * <p>
     * The entity's durability is reduced by a fixed amount each tick. When durability
     * reaches zero or less, the status is marked as degraded and will no longer remain active.
     *
     * @param currEntity the entity currently carrying this status
     * @param location the location of the current entity
     */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        currEntity.modifyStatistic(ItemStatistics.DURABILITY, StatisticOperations.DECREASE, DEGRADE_VALUE);
        System.out.println(currEntity + " is infected. Uses left decreased by " +  DEGRADE_VALUE);
        if (currEntity.getStatistic(ItemStatistics.DURABILITY) <= 0) {
            degraded = true;
            System.out.println(currEntity + " has fully degraded and can no longer be used.");
        }
    }

    /**
     * Indicates whether this degradation status should continue to be processed.
     *
     * @return {@code true} while the item still has durability remaining; otherwise {@code false}
     */
    @Override
    public boolean isStatusActive() {
        return !degraded;
    }
}
