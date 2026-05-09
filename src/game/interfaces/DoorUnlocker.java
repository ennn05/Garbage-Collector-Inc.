package game.interfaces;

/**
 * Represents an object that can unlock an Unlockable target.
 */
public interface DoorUnlocker {

    /**
     * Check whether this object can unlock the given target.
     *
     * @param unlockable the target to be unlocked
     * @return true if this object can unlock the target
     */
    boolean canUnlock(Unlockable unlockable);

    /**
     * Unlock the given target.
     *
     * @param unlockable the target to be unlocked
     */
    void unlock(Unlockable unlockable);
}