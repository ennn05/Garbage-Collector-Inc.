package game.status;
import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

/**
 * A poison status that damages an actor over multiple turns.
 */
public class PoisonStatus implements Status {
    private static final int DAMAGE_PER_TURN = 1;
    private int turnsRemaining;

    public PoisonStatus(int turnsRemaining) {
        this.turnsRemaining = turnsRemaining;
    }

    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        if (turnsRemaining > 0) {
            currEntity.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.DECREASE, DAMAGE_PER_TURN);
            turnsRemaining--;
        }
    }

    @Override
    public boolean isStatusActive() {
        return turnsRemaining > 0;
    }
}