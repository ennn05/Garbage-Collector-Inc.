package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.AccessLevel;

/**
 * Represents a ground that can be unlocked.
 */
public interface Unlockable {
    /**
     * Check whether the object is already unlocked.
     *
     * @return true if unlocked, false otherwise
     */
    boolean isUnlocked();

    /**
     * Unlock the object.
     */
    void unlock();

    /**
     * Lock the object again.
     */
    void lock();

    /**
     * Get the clearance level required to unlock this object.
     *
     * @return the required access level
     */
    AccessLevel getRequiredClearance();

    /**
     * Apply the immediate effect that occurs when this object is unlocked.
     * By default, unlocking has no extra effect.
     *
     * @param actor the actor unlocking this object
     * @param location the location of this unlockable object
     * @return result description
     */
    default String onUnlocked(Actor actor, Location location) {
        return "";
    }
}