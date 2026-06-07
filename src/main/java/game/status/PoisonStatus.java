package game.status;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

/**
 * A poison status that damages an entity over multiple turns.
 */
public class PoisonStatus implements Status {
    private static final int DEFAULT_DAMAGE_PER_TURN = 1;

    private final int damagePerTurn;
    private int turnsRemaining;

    /**
     * Constructor using the default poison damage.
     *
     * @param turnsRemaining number of turns the poison lasts
     */
    public PoisonStatus(int turnsRemaining) {
        this(turnsRemaining, DEFAULT_DAMAGE_PER_TURN);
    }

    /**
     * Constructor using a custom poison damage.
     *
     * @param turnsRemaining number of turns the poison lasts
     * @param damagePerTurn damage dealt each turn
     */
    public PoisonStatus(int turnsRemaining, int damagePerTurn) {
        this.turnsRemaining = turnsRemaining;
        this.damagePerTurn = damagePerTurn;
    }

    /**
     * Apply poison damage and reduce the remaining duration.
     *
     * @param currEntity the entity affected by this status
     * @param location the location of the entity
     */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        if (turnsRemaining > 0) {
            currEntity.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.DECREASE, damagePerTurn);
            turnsRemaining--;

            System.out.println(currEntity + " suffers " + damagePerTurn
                    + " poison damage. Poison has " + turnsRemaining
                    + " turn(s) remaining.");
        }
    }

    /**
     * Check whether the poison is still active.
     *
     * @return true if the poison has turns remaining
     */
    @Override
    public boolean isStatusActive() {
        return turnsRemaining > 0;
    }
}