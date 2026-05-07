package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import game.interfaces.Unlockable;

/**
 * Its primary purpose in the universe is to halt the progress of underpaid
 * {@code ContractedWorker}s until they can produce the correct rectangular
 * piece of plastic.
 */
public class Door extends Ground implements Unlockable {
    private boolean isUnlocked = false;

    public Door() {
        super('=', "Door");
    }

    @Override
    public boolean isUnlocked() {
        return isUnlocked;
    }

    @Override
    public void unlock() {
        this.isUnlocked = true;
    }

    @Override
    public void lock() {
        this.isUnlocked = false;
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return isUnlocked;
    }
}