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
}