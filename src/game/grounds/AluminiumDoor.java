package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.AccessLevel;

/**
 * An aluminium door that requires Access Level 1 or higher to open.
 * When unlocked, it delivers a faulty electrical shock dealing 2 damage to the actor.
 */
public class AluminiumDoor extends Door {
    private static final int SHOCK_DAMAGE = 2;

    /**
     * Constructor for AluminiumDoor.
     */
    public AluminiumDoor() {
        super('=', "Aluminium Door");
    }

    /**
     * Get the required access level for this door.
     *
     * @return AccessLevel.LEVEL_1
     */
    @Override
    public AccessLevel getRequiredClearance() {
        return AccessLevel.LEVEL_1;
    }

    /**
     * When unlocked, deal exactly 2 damage to the actor from electrical shock.
     *
     * @param actor the actor unlocking this object
     * @param location the location of this unlockable object
     * @return result description
     */
    @Override
    public String onUnlocked(Actor actor, Location location) {
        actor.hurt(SHOCK_DAMAGE);
        return actor + " is shocked by the Aluminium Door for " + SHOCK_DAMAGE + " damage.";
    }
}