package game;

import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.actors.Actor;

/**
 * A status effect representing being on fire or in hot conditions.
 * Applies damage over time and expires after a set number of turns.
 */
public class BurnStatus implements Status {
    private int turnsRemaining;
    private int damagePerTurn;

    /**
     * Constructor for BurnStatus.
     * @param turnsRemaining number of turns this status lasts
     * @param damagePerTurn damage dealt per turn
     */
    public BurnStatus(int turnsRemaining, int damagePerTurn) {
        this.turnsRemaining = turnsRemaining;
        this.damagePerTurn = damagePerTurn;
    }

    /**
     * Apply damage to the actor and decrement the counter.
     * @param actor the actor affected by burn status
     */
    public void tick(Actor actor) {
        actor.takeDamage(damagePerTurn);
        turnsRemaining--;
    }

    /**
     * Check if this status has expired.
     * @return true if turnsRemaining <= 0, false otherwise
     */
    @Override
    public boolean isExpired() {
        return turnsRemaining <= 0;
    }
}
