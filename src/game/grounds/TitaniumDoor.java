package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import game.enums.AccessLevel;

/**
 * A titanium door that requires Access Level 3 to open.
 * When unlocked, it triggers a decontamination sequence that heals the actor for 5 health points.
 */
public class TitaniumDoor extends Door {

    /**
     * Constructor for TitaniumDoor.
     */
    public TitaniumDoor() {
        super('M', "Titanium Door");
    }

    /**
     * Get the required access level for this door.
     * @return AccessLevel.LEVEL_3
     */
    @Override
    public AccessLevel getRequiredClearance() {
        return AccessLevel.LEVEL_3;
    }

    /**
     * When unlocked, heal the actor for 5 health points.
     * @param actor the actor unlocking the door
     */
    @Override
    public void onUnlock(Actor actor) {
        actor.heal(5);
    }
}
