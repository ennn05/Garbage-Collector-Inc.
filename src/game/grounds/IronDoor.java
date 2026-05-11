package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.AccessLevel;

/**
 * An iron door that requires Access Level 2 or higher to open.
 * When unlocked, the rusted machinery overheats and sets all adjacent floor tiles on fire.
 */
public class IronDoor extends Door {

    /**
     * Constructor for IronDoor.
     */
    public IronDoor() {
        super('N', "Iron Door");
    }

    /**
     * Get the required access level for this door.
     * @return AccessLevel.LEVEL_2
     */
    @Override
    public AccessLevel getRequiredClearance() {
        return AccessLevel.LEVEL_2;
    }

    /**
     * When unlocked, set all adjacent floor tiles on fire (2 turns).
     * @param actor the actor unlocking the door
     */
    @Override
    public void onUnlock(Actor actor) {
        // Unlock effect handled, fire-setting done in UnlockDoorAction
    }

    /**
     * Set adjacent floor tiles on fire. Called from UnlockDoorAction.
     * @param location the location of this door
     */
    public void setAdjacentTilesOnFire(Location location) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            if (adjacent.getGround() instanceof Floor) {
                adjacent.setGround(new Fire(new Floor()));
            }
        }
    }
}
