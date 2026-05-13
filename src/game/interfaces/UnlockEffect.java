package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents an unlockable object that has an immediate effect when unlocked.
 */
public interface UnlockEffect {

    /**
     * Apply the effect that happens immediately after this object is unlocked.
     *
     * @param actor the actor unlocking the object
     * @param location the location of the unlocked object
     * @return result description of the unlock effect
     */
    String onUnlocked(Actor actor, Location location);
}