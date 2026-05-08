package game;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * An iron door that requires Clearance Level 2 or higher to open.
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
     * Get the required clearance level for this door.
     * @return ClearanceLevel.LEVEL_2
     */
    @Override
    public ClearanceLevel getRequiredClearance() {
        return ClearanceLevel.LEVEL_2;
    }

    /**
     * When unlocked, set all adjacent floor tiles on fire (2 turns).
     * @param actor the actor unlocking the door
     */
    @Override
    public void onUnlock(Actor actor) {
        // Note: This method will be called from UnlockDoorAction which has access to the map
        // For now, this is a placeholder. The actual fire-setting will be done in UnlockDoorAction
        // to ensure we have access to the GameMap and Location.
    }

    /**
     * Set adjacent floor tiles on fire. Called from UnlockDoorAction.
     * @param location the location of this door
     * @param map the game map
     */
    public void setAdjacentTilesOnFire(Location location, GameMap map) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            if (adjacent.getGround() instanceof Floor) {
                adjacent.setGround(new Fire(2));
            }
        }
    }
}
