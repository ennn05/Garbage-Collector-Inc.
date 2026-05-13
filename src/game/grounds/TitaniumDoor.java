package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.AccessLevel;

/**
 * A titanium door that requires Access Level 3 to open.
 * When unlocked, it triggers a decontamination sequence that heals the actor for 5 health points.
 */
public class TitaniumDoor extends Door {
    private static final int HEAL_AMOUNT = 5;

    /**
     * Constructor for TitaniumDoor.
     */
    public TitaniumDoor() {
        super('M', "Titanium Door");
    }

    /**
     * Get the required access level for this door.
     *
     * @return AccessLevel.LEVEL_3
     */
    @Override
    public AccessLevel getRequiredClearance() {
        return AccessLevel.LEVEL_3;
    }

    /**
     * When unlocked, heal the actor for 5 health points.
     *
     * @param actor the actor unlocking this object
     * @param location the location of this unlockable object
     * @return result description
     */
    @Override
    public String onUnlocked(Actor actor, Location location) {
        actor.heal(HEAL_AMOUNT);
        return actor + " is healed by the Titanium Door decontamination sequence for "
                + HEAL_AMOUNT + " health.";
    }
}