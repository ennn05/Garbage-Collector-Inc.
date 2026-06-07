package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Interface for items that can be deposited at the supercomputer to increase company quota.
 */
public interface Depositable {
    /**
     * Check if an actor can deposit this item.
     *
     * @param actor the actor attempting to deposit
     * @return true if the actor can deposit this item
     */
    boolean canBeDeposited(Actor actor);

    /**
     * Handle the deposit action and return the result message.
     *
     * @param actor the actor performing the deposit
     * @param location the location of the supercomputer
     * @return result description of the deposit
     */
    String onDeposit(Actor actor, Location location);

    /**
     * Get the company credits rewarded for depositing this item.
     *
     * @return the company credits reward
     */
    int getDepositReward();
}
