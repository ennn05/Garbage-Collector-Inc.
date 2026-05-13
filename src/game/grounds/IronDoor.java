package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.AccessLevel;
import game.utility.FireSpawner;

/**
 * An iron door that requires Access Level 2 or higher to open.
 * When unlocked, the rusted machinery overheats and creates fire around the door.
 */
public class IronDoor extends Door {
    private static final int FIRE_DURATION = 2;

    private final FireSpawner fireSpawner = new FireSpawner();

    /**
     * Constructor for IronDoor.
     */
    public IronDoor() {
        super('N', "Iron Door");
    }

    /**
     * Get the required access level for this door.
     *
     * @return AccessLevel.LEVEL_2
     */
    @Override
    public AccessLevel getRequiredClearance() {
        return AccessLevel.LEVEL_2;
    }

    /**
     * When unlocked, create fire around the door for 2 turns.
     *
     * @param actor the actor unlocking this object
     * @param location the location of this unlockable object
     * @return result description
     */
    @Override
    public String onUnlocked(Actor actor, Location location) {
        fireSpawner.spawnAround(location, FIRE_DURATION);
        return "The Iron Door overheats and creates fire around itself for "
                + FIRE_DURATION + " turns.";
    }
}