package game.status;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

/**
 * A burn status that damages an entity over multiple turns.
 */
public class BurnStatus implements Status {
    private final int damagePerTurn;
    private int turnsRemaining;

    /**
     * Constructor.
     *
     * @param turnsRemaining number of turns the burn lasts
     * @param damagePerTurn damage dealt each turn
     */
    public BurnStatus(int turnsRemaining, int damagePerTurn) {
        this.turnsRemaining = turnsRemaining;
        this.damagePerTurn = damagePerTurn;
    }

    /**
     * Apply burn damage and reduce the remaining duration.
     *
     * @param currEntity the entity affected by this status
     * @param location the location of the entity
     */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        if (turnsRemaining > 0) {
            currEntity.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.DECREASE, damagePerTurn);
            turnsRemaining--;
        }
    }

    /**
     * Check whether the burn is still active.
     *
     * @return true if the burn has turns remaining
     */
    @Override
    public boolean isStatusActive() {
        return turnsRemaining > 0;
    }
}