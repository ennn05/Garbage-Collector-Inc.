package game;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * A titanium door that requires Clearance Level 3 to open.
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
     * Get the required clearance level for this door.
     * @return ClearanceLevel.LEVEL_3
     */
    @Override
    public ClearanceLevel getRequiredClearance() {
        return ClearanceLevel.LEVEL_3;
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
