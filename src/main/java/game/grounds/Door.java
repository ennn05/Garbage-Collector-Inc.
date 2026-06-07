package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import game.enums.AccessLevel;
import game.interfaces.Unlockable;

/**
 * A locked ground that requires a specific access clearance before actors can enter.
 */
public abstract class Door extends Ground implements Unlockable {
    protected boolean isUnlocked = false;

    /**
     * Constructor for Door.
     *
     * @param displayChar the character to display
     * @param name the name of the door
     */
    public Door(char displayChar, String name) {
        super(displayChar, name);
    }

    /**
     * Get the clearance level required to open this door.
     *
     * @return the required AccessLevel
     */
    @Override
    public abstract AccessLevel getRequiredClearance();

    /**
     * Check whether this door is unlocked.
     *
     * @return true if unlocked
     */
    @Override
    public boolean isUnlocked() {
        return isUnlocked;
    }

    /**
     * Unlock this door.
     */
    @Override
    public void unlock() {
        this.isUnlocked = true;
    }

    /**
     * Lock this door again.
     */
    @Override
    public void lock() {
        this.isUnlocked = false;
    }

    /**
     * If the door is unlocked, any actor can step into the door.
     *
     * @param actor the Actor to check
     * @return true if the door is unlocked, false otherwise
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return isUnlocked;
    }

    /**
     * If the door is unlocked, any objects can pass through when thrown.
     *
     * @return true if the door is unlocked, false otherwise
     */
    @Override
    public boolean blocksThrownObjects() {
        return !isUnlocked;
    }
}