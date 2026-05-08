package game;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;

/**
 * Its primary purpose in the universe is to halt the progress of underpaid
 * {@code ContractedWorker}s until they can produce the correct rectangular
 * piece of plastic.
 * 
 * This is now an abstract class that requires subclasses to implement
 * the required clearance level and unlock behavior.
 */
public abstract class Door extends Ground {
    protected boolean isUnlocked = false;

    /**
     * Constructor for Door.
     * @param displayChar the character to display
     * @param name the name of the door
     */
    public Door(char displayChar, String name) {
        super(displayChar, name);
    }

    /**
     * Get the clearance level required to open this door.
     * @return the required ClearanceLevel
     */
    public abstract ClearanceLevel getRequiredClearance();

    /**
     * Execute the unlock effect of this door on the actor.
     * @param actor the actor unlocking the door
     */
    public abstract void onUnlock(Actor actor);

    /**
     * if the door is unlocked, any actor can step into the door
     * @param actor the Actor to check
     * @return true if the door is unlocked, false otherwise.
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return isUnlocked;
    }
}
