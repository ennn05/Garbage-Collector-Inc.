package game.interfaces;

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
     * Apply the immediate effect that occurs when this object is unlocked.
     * By default, unlocking has no extra effect.
     *
     * @param actor the actor unlocking this object
     * @param location the location of this unlockable object
     * @return result description
     */
    default String onUnlocked(edu.monash.fit2099.engine.actors.Actor actor,
                              edu.monash.fit2099.engine.positions.Location location) {
        return "";
    }
}