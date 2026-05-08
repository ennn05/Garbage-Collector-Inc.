package game;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * An aluminium door that requires Clearance Level 1 or higher to open.
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
     * Get the required clearance level for this door.
     * @return ClearanceLevel.LEVEL_1
     */
    @Override
    public ClearanceLevel getRequiredClearance() {
        return ClearanceLevel.LEVEL_1;
    }

    /**
     * When unlocked, deal exactly 2 damage to the actor from electrical shock.
     * @param actor the actor unlocking the door
     */
    @Override
    public void onUnlock(Actor actor) {
        actor.takeDamage(2);
    }
}
