package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import game.enums.AccessLevel;
import game.interfaces.Unlockable;

/**
 * An aluminium door that requires Access Level 1 or higher to open.
 * When unlocked, it delivers a faulty electrical shock dealing 2 damage to the actor.
 */
public class AluminiumDoor extends Door {

    /**
     * Constructor for AluminiumDoor.
     */
    public AluminiumDoor() {
        super('=', "Aluminium Door");
    }

    /**
     * Get the required access level for this door.
     * @return AccessLevel.LEVEL_1
     */
    @Override
    public AccessLevel getRequiredClearance() {
        return AccessLevel.LEVEL_1;
    }

    /**
     * When unlocked, deal exactly 2 damage to the actor from electrical shock.
     * @param actor the actor unlocking the door
     */
    @Override
    public void onUnlock(Actor actor) {
        actor.hurt(2);
    }
}
